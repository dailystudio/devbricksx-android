# DevBricksX CLI (`devbricksx`)

A command-line interface tool written in Kotlin to quickly create and bootstrap new Android projects utilizing the DevBricksX libraries and architectural components.

## Features

- **Project Generation**: Scaffold a multi-module Android project ready for development using DevBricksX.
- **Template-Based**: Bundles the official DevBricksX Android template inside the CLI distribution.
- **Developer-Friendly Template**: The template project inside `template/` is an actual, testable Android project aligned with the root `devbricksx-android` project configuration (Gradle wrapper 9.7, AGP 9.3, Kotlin 2.3.10, SDK 37, JDK 21). You can open and test it directly in Android Studio.
- **Flexible UI Targets**:
  - `compose` (default): Jetpack Compose only (`app-compose` and `core`).
  - `views`: Android Views (XML) only (`app` and `core`).
  - `all`: Scaffolds both Android Views (XML) and Jetpack Compose app modules.
- **Modular Components**:
  - Optional `ndk` extra module (`core-native` module with CMake and JNI glue).
- **Theme & Branding**:
  - Customize application theme primary color (`--theme-color`).
  - Automatic package renaming and source code alignment.
- **Full Icon Generation (SVG & PNG)**:
  - **SVG Icons**: Automatically converts SVG files to Android Vector Drawables (`ic_launcher_foreground.xml`) centered and scaled in 108dp canvas, renders crisp 512x512 composites for Play Store, and generates all legacy/round mipmap PNG assets.
  - **PNG Icons**: Supports transparent PNG logos (composited over the theme background) and opaque icons, generating adaptive foregrounds, Play Store 512x512 icons, and all mipmap densities.
  - Fully self-contained without external dependencies (`magick`, `rsvg-convert`, etc.).

## Installation

### Quick Install
Run the installer script from the root repository or inside `devbricksx-cli`:
```bash
# From root repository:
./install-cli.sh

# Or from devbricksx-cli directory:
cd devbricksx-cli
./install.sh
```

The installer will:
1. **Check Java Environment**: DevBricksX CLI requires Java 21 or higher. The installer checks your local Java runtime (`JAVA_HOME` or `PATH`).
2. **Download Dedicated JRE If Needed**: If no compatible Java 21+ is found on your PC, the installer automatically downloads Eclipse Adoptium Temurin JRE 21 for your OS and architecture into `~/.devbricksx/jre`, ensuring the CLI always has its required JRE. You can also force a dedicated JRE with `--download-jre`.
3. **Install Binary & Libraries**: Deploys `devbricksx` binary into `~/.devbricksx/bin` and libraries into `~/.devbricksx/lib`.
4. **Configure Shell Auto-Completion**: Automatically configures tab auto-completion for your shell (`zsh`, `bash`, `fish`) and adds `~/.devbricksx/bin` to your `PATH` in `~/.zshrc` / `~/.bashrc`.

### Installer Options
```bash
./install.sh [options]

Options:
  -d, --prefix <dir>       Installation target directory (default: ~/.devbricksx)
      --download-jre       Force download and bundle a dedicated Java 21 JRE
      --no-completion      Skip modifying shell profile for auto-completion
  -s, --shell <shell>      Specify target shell for completions (zsh, bash, fish)
  -h, --help               Show installer options
      --uninstall          Remove DevBricksX CLI from ~/.devbricksx
```

### Manual Shell Auto-Completion
If you prefer manual shell completion configuration:
- **Zsh**:
  ```zsh
  # In ~/.zshrc
  export PATH="$HOME/.devbricksx/bin:$PATH"
  source <(devbricksx completion zsh)
  ```
- **Bash**:
  ```bash
  # In ~/.bashrc
  export PATH="$HOME/.devbricksx/bin:$PATH"
  source <(devbricksx completion bash)
  ```
- **Fish**:
  ```fish
  devbricksx completion fish > ~/.config/fish/completions/devbricksx.fish
  ```

### Build Distribution Manually
```bash
cd devbricksx-cli
./gradlew installDist
```
The executable binary will be generated at `build/install/devbricksx/bin/devbricksx`.

### Run via Gradle
```bash
./gradlew run --args="--help"
```

## Usage

### Display Help
```bash
devbricksx --help
```
Output:
```
Usage: devbricksx [<options>] <command> [<args>]...

  DevBricksX CLI - Command-line tool for creating and managing Android projects
  with DevBricksX.

Options:
  --version   Show the version and exit
  -h, --help  Show this message and exit

Commands:
  create  Create a new Android project using DevBricksX template.
```

### Create a Project (`create`)
```bash
devbricksx create --help
```
Options:
- `-n`, `--name <text>`: Application name (e.g. `"My Application"`). Required.
- `-p`, `--package <text>`: Android package name (e.g. `"com.example.myapp"`). Required.
- `-o`, `--output <text>`: Output directory for the generated project (default: `./<appName>`).
- `-u`, `--ui <text>`: UI target: `compose`, `views`, `all` (default: `compose`).
- `-e`, `--extra-modules <text>`: Extra modules to include, comma-separated (e.g. `ndk`).
- `-t`, `--theme-color <text>`: Primary theme color hex (e.g. `#008577` or `#3F51B5`).
- `-i`, `--icon <text>`: Path to custom app icon file (`.svg` or `.png`).
- `--icon-fg-color <text>`: Color tint for icon foreground (hex, e.g. `#FFFFFF`, or `none` to preserve original colors). Defaults to `#FFFFFF` for SVG icons.
- `--icon-scale <int>`: Foreground scale percentage in icon composition (1-100, default: `70`).
- `-h`, `--help`: Show command help.

#### Examples

1. **Create project with SVG icon**:
```bash
devbricksx create \
  -n "My App" \
  -p "com.example.myapp" \
  -o ./MyApp \
  -u compose \
  -t "#008577" \
  -i /path/to/icon.svg
```

2. **Create full project with PNG icon and NDK module**:
```bash
devbricksx create \
  -n "My App" \
  -p "com.example.myapp" \
  -o ./MyApp \
  -u all \
  -e ndk \
  -t "#3F51B5" \
  -i /path/to/icon.png
```

3. **Create Views (XML)-only project**:
```bash
devbricksx create -n "Views App" -p "com.example.viewsapp" -u views
```

## Template Project

The template project is located in `template/`. You can open and test it independently:
```bash
cd devbricksx-cli/template
./gradlew assembleDebug
```
When building the CLI, the `packageTemplate` task packages the clean template project into a resource ZIP (`template.zip`) which is bundled into the CLI binary.
