#!/usr/bin/env bash
#
# DevBricksX CLI Installer
# Checks Java environment (requires Java 21+), downloads a standalone JRE if missing,
# installs devbricksx binary and libraries, and sets up shell auto-completion.
#

set -euo pipefail

# Colors for output
RED=$(printf '\033[0;31m')
GREEN=$(printf '\033[0;32m')
YELLOW=$(printf '\033[1;33m')
BLUE=$(printf '\033[0;34m')
BOLD=$(printf '\033[1m')
NC=$(printf '\033[0m') # No Color

info() {
    printf "${BLUE}==>${NC} ${BOLD}%s${NC}\n" "$*"
}

success() {
    printf "${GREEN}✓${NC} %s\n" "$*"
}

warn() {
    printf "${YELLOW}!${NC} %s\n" "$*"
}

error() {
    printf "${RED}✗ Error:${NC} %s\n" "$*" >&2
    exit 1
}

# Defaults
INSTALL_DIR="${DEVBRICKSX_INSTALL_DIR:-$HOME/.devbricksx}"
FORCE_DOWNLOAD_JRE=false
SETUP_COMPLETION=true
TARGET_SHELL=""

usage() {
    cat <<EOF
DevBricksX CLI Installer

Usage:
  ./install.sh [options]

Options:
  -d, --prefix <dir>       Installation target directory (default: ~/.devbricksx)
      --download-jre       Force download and bundle a dedicated Java 21 JRE
      --no-completion      Skip modifying shell profile for auto-completion
  -s, --shell <shell>      Specify target shell for completions (zsh, bash, fish)
  -h, --help               Show this help message
      --uninstall          Remove DevBricksX CLI from ~/.devbricksx

EOF
    exit 0
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        -d|--prefix)
            INSTALL_DIR="$2"
            shift 2
            ;;
        --download-jre)
            FORCE_DOWNLOAD_JRE=true
            shift
            ;;
        --no-completion)
            SETUP_COMPLETION=false
            shift
            ;;
        -s|--shell)
            TARGET_SHELL="$2"
            shift 2
            ;;
        --uninstall)
            info "Uninstalling DevBricksX CLI from $INSTALL_DIR..."
            rm -rf "$INSTALL_DIR"
            success "Removed $INSTALL_DIR"
            warn "Please remove any DevBricksX PATH or completion entries from your shell configuration files (~/.zshrc, ~/.bashrc)."
            exit 0
            ;;
        -h|--help)
            usage
            ;;
        *)
            error "Unknown argument: $1 (use --help for options)"
            ;;
    esac
done

# Resolve absolute path for INSTALL_DIR
mkdir -p "$INSTALL_DIR"
INSTALL_DIR="$(cd "$INSTALL_DIR" && pwd)"

# Detect source directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [[ -f "$SCRIPT_DIR/build.gradle.kts" ]]; then
    CLI_SRC_DIR="$SCRIPT_DIR"
elif [[ -f "$SCRIPT_DIR/devbricksx-cli/build.gradle.kts" ]]; then
    CLI_SRC_DIR="$SCRIPT_DIR/devbricksx-cli"
elif [[ -f "$(pwd)/build.gradle.kts" ]]; then
    CLI_SRC_DIR="$(pwd)"
elif [[ -f "$(pwd)/devbricksx-cli/build.gradle.kts" ]]; then
    CLI_SRC_DIR="$(pwd)/devbricksx-cli"
else
    CLI_SRC_DIR=""
fi

info "Installing DevBricksX CLI into: $INSTALL_DIR"

# Detect OS and Architecture
OS_TYPE="$(uname -s)"
ARCH_TYPE="$(uname -m)"

case "$OS_TYPE" in
    Darwin)
        ADOPTIUM_OS="mac"
        ;;
    Linux)
        ADOPTIUM_OS="linux"
        ;;
    *)
        error "Unsupported operating system: $OS_TYPE"
        ;;
esac

case "$ARCH_TYPE" in
    x86_64|amd64)
        ADOPTIUM_ARCH="x64"
        ;;
    arm64|aarch64)
        ADOPTIUM_ARCH="aarch64"
        ;;
    *)
        error "Unsupported architecture: $ARCH_TYPE"
        ;;
esac

# Function to get Java major version
get_java_major_version() {
    local java_bin="$1"
    if [[ ! -x "$java_bin" ]]; then
        echo "0"
        return
    fi
    local raw_ver
    raw_ver="$("$java_bin" -version 2>&1 | sed -n 's/.*version "\([^"]*\)".*/\1/p')"
    if [[ -z "$raw_ver" ]]; then
        echo "0"
        return
    fi
    local major
    major="$(echo "$raw_ver" | sed -e 's/^1\.//' -e 's/\..*//' -e 's/-.*//')"
    echo "${major:-0}"
}

# Function to find bundled JRE inside a directory
find_bundled_java() {
    local jre_dir="$1"
    if [[ -x "$jre_dir/Contents/Home/bin/java" ]]; then
        echo "$jre_dir/Contents/Home/bin/java"
    elif [[ -x "$jre_dir/bin/java" ]]; then
        echo "$jre_dir/bin/java"
    fi
}

# Check for existing valid Java
ACTIVE_JAVA=""
JAVA_SOURCE=""

# 1. Check existing dedicated JRE in INSTALL_DIR
if [[ -d "$INSTALL_DIR/jre" ]]; then
    EXISTING_BUNDLED_JAVA="$(find_bundled_java "$INSTALL_DIR/jre")"
    if [[ -n "$EXISTING_BUNDLED_JAVA" ]]; then
        BUNDLED_VER="$(get_java_major_version "$EXISTING_BUNDLED_JAVA")"
        if [[ "$BUNDLED_VER" -ge 21 ]]; then
            ACTIVE_JAVA="$EXISTING_BUNDLED_JAVA"
            JAVA_SOURCE="Dedicated JRE in $INSTALL_DIR/jre"
        fi
    fi
fi

# 2. Check system Java if not forced to download
if [[ -z "$ACTIVE_JAVA" && "$FORCE_DOWNLOAD_JRE" != "true" ]]; then
    if [[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/java" ]]; then
        JH_VER="$(get_java_major_version "$JAVA_HOME/bin/java")"
        if [[ "$JH_VER" -ge 21 ]]; then
            ACTIVE_JAVA="$JAVA_HOME/bin/java"
            JAVA_SOURCE="JAVA_HOME ($JAVA_HOME)"
        fi
    fi

    if [[ -z "$ACTIVE_JAVA" ]] && command -v java >/dev/null 2>&1; then
        PATH_JAVA="$(command -v java)"
        PATH_VER="$(get_java_major_version "$PATH_JAVA")"
        if [[ "$PATH_VER" -ge 21 ]]; then
            ACTIVE_JAVA="$PATH_JAVA"
            JAVA_SOURCE="PATH ($PATH_JAVA)"
        fi
    fi
fi

# Download JRE if needed
if [[ "$FORCE_DOWNLOAD_JRE" == "true" || -z "$ACTIVE_JAVA" ]]; then
    if [[ "$FORCE_DOWNLOAD_JRE" == "true" ]]; then
        info "Dedicated JRE requested via --download-jre."
    else
        warn "No compatible Java 21+ runtime found on your system (DevBricksX CLI requires Java 21+)."
    fi

    info "Downloading Eclipse Adoptium Temurin JRE 21 for ${ADOPTIUM_OS}-${ADOPTIUM_ARCH}..."
    JRE_URL="https://api.adoptium.net/v3/binary/latest/21/ga/${ADOPTIUM_OS}/${ADOPTIUM_ARCH}/jre/hotspot/normal/eclipse"
    
    TMP_DIR="$(mktemp -d 2>/dev/null || mktemp -d -t 'devbricksx_jre')"
    TAR_FILE="$TMP_DIR/jre.tar.gz"

    if command -v curl >/dev/null 2>&1; then
        curl -fSL --progress-bar -o "$TAR_FILE" "$JRE_URL"
    elif command -v wget >/dev/null 2>&1; then
        wget -q --show-progress -O "$TAR_FILE" "$JRE_URL"
    else
        error "Neither 'curl' nor 'wget' found. Please install one to download the JRE."
    fi

    info "Extracting dedicated JRE into $INSTALL_DIR/jre..."
    rm -rf "$INSTALL_DIR/jre"
    mkdir -p "$INSTALL_DIR/jre"
    tar -xzf "$TAR_FILE" --strip-components=1 -C "$INSTALL_DIR/jre"
    rm -rf "$TMP_DIR"

    ACTIVE_JAVA="$(find_bundled_java "$INSTALL_DIR/jre")"
    if [[ -z "$ACTIVE_JAVA" || ! -x "$ACTIVE_JAVA" ]]; then
        error "Failed to locate java executable inside extracted JRE at $INSTALL_DIR/jre"
    fi
    JAVA_SOURCE="Downloaded dedicated JRE in $INSTALL_DIR/jre"
    success "Dedicated JRE 21 installed successfully at $INSTALL_DIR/jre"
else
    success "Found compatible Java runtime: $($ACTIVE_JAVA -version 2>&1 | head -n 1) via $JAVA_SOURCE"
fi

# If using dedicated JRE, derive its JAVA_HOME
if [[ "$ACTIVE_JAVA" == "$INSTALL_DIR/jre"* ]]; then
    if [[ -d "$INSTALL_DIR/jre/Contents/Home" ]]; then
        BUILD_JAVA_HOME="$INSTALL_DIR/jre/Contents/Home"
    else
        BUILD_JAVA_HOME="$INSTALL_DIR/jre"
    fi
elif [[ -n "${JAVA_HOME:-}" ]]; then
    BUILD_JAVA_HOME="$JAVA_HOME"
else
    BUILD_JAVA_HOME="$(cd "$(dirname "$ACTIVE_JAVA")/.." && pwd)"
fi

# Build CLI if source is present
if [[ -n "$CLI_SRC_DIR" ]]; then
    info "Building DevBricksX CLI from source ($CLI_SRC_DIR)..."
    (
        cd "$CLI_SRC_DIR"
        JAVA_HOME="$BUILD_JAVA_HOME" ./gradlew installDist -q
    )
    DIST_DIR="$CLI_SRC_DIR/build/install/devbricksx"
else
    error "DevBricksX source repository not found. Please run install.sh from the devbricksx-android repository."
fi

# Copy distribution files to INSTALL_DIR
info "Installing files to $INSTALL_DIR..."
mkdir -p "$INSTALL_DIR/bin" "$INSTALL_DIR/lib" "$INSTALL_DIR/completions"

cp -R "$DIST_DIR/bin/"* "$INSTALL_DIR/bin/"
cp -R "$DIST_DIR/lib/"* "$INSTALL_DIR/lib/"
chmod +x "$INSTALL_DIR/bin/devbricksx"

# Generate auto-completion scripts
info "Generating shell auto-completion scripts..."
"$INSTALL_DIR/bin/devbricksx" completion zsh > "$INSTALL_DIR/completions/devbricksx.zsh"
"$INSTALL_DIR/bin/devbricksx" completion bash > "$INSTALL_DIR/completions/devbricksx.bash"
"$INSTALL_DIR/bin/devbricksx" completion fish > "$INSTALL_DIR/completions/devbricksx.fish"
success "Completion scripts generated in $INSTALL_DIR/completions/"

# Setup shell configuration
if [[ "$SETUP_COMPLETION" == "true" ]]; then
    info "Configuring shell PATH and auto-completion..."

    # Configure ZSH
    ZSHRC="$HOME/.zshrc"
    if [[ -f "$ZSHRC" || "$TARGET_SHELL" == "zsh" || "$SHELL" == *"zsh"* ]]; then
        touch "$ZSHRC"
        if ! grep -q "DEVBRICKSX_INSTALL_DIR\|devbricksx.zsh" "$ZSHRC"; then
            cat >> "$ZSHRC" <<EOF

# DevBricksX CLI
export PATH="$INSTALL_DIR/bin:\$PATH"
[[ -f "$INSTALL_DIR/completions/devbricksx.zsh" ]] && source "$INSTALL_DIR/completions/devbricksx.zsh"
EOF
            success "Configured PATH and completion in $ZSHRC"
        else
            success "DevBricksX configuration already present in $ZSHRC"
        fi
    fi

    # Configure Bash
    BASHRC=""
    if [[ -f "$HOME/.bashrc" ]]; then
        BASHRC="$HOME/.bashrc"
    elif [[ -f "$HOME/.bash_profile" ]]; then
        BASHRC="$HOME/.bash_profile"
    fi

    if [[ -n "$BASHRC" || "$TARGET_SHELL" == "bash" || "$SHELL" == *"bash"* ]]; then
        [[ -z "$BASHRC" ]] && BASHRC="$HOME/.bashrc"
        touch "$BASHRC"
        if ! grep -q "devbricksx.bash" "$BASHRC"; then
            cat >> "$BASHRC" <<EOF

# DevBricksX CLI
export PATH="$INSTALL_DIR/bin:\$PATH"
[[ -f "$INSTALL_DIR/completions/devbricksx.bash" ]] && source "$INSTALL_DIR/completions/devbricksx.bash"
EOF
            success "Configured PATH and completion in $BASHRC"
        fi
    fi

    # Configure Fish
    FISH_COMP_DIR="$HOME/.config/fish/completions"
    if [[ -d "$HOME/.config/fish" || "$TARGET_SHELL" == "fish" || "$SHELL" == *"fish"* ]]; then
        mkdir -p "$FISH_COMP_DIR"
        cp "$INSTALL_DIR/completions/devbricksx.fish" "$FISH_COMP_DIR/devbricksx.fish"
        success "Configured Fish completion in $FISH_COMP_DIR/devbricksx.fish"
    fi
fi

# Verify installation
info "Verifying installation..."
CLI_VERSION="$("$INSTALL_DIR/bin/devbricksx" --version 2>&1 || echo "unknown")"
success "DevBricksX CLI ($CLI_VERSION) is installed and operational!"

cat <<EOF

====================================================================
${GREEN}${BOLD}DevBricksX CLI successfully installed!${NC}
====================================================================

Binary location:
  $INSTALL_DIR/bin/devbricksx

Java Runtime:
  $JAVA_SOURCE

Shell Completions:
  $INSTALL_DIR/completions/

To enable completions and PATH in your current terminal session, run:
  ${BOLD}source ~/.zshrc${NC}    # if using zsh
  ${BOLD}source ~/.bashrc${NC}   # if using bash

Or simply open a new terminal window.

Verify with:
  ${BOLD}devbricksx --help${NC}
  ${BOLD}devbricksx create --help${NC}

Try tab completion:
  ${BOLD}devbricksx <TAB>${NC}
  ${BOLD}devbricksx create --ui <TAB>${NC}
====================================================================
EOF
