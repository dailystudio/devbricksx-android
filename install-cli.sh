#!/usr/bin/env bash
# DevBricksX CLI Installer wrapper
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$SCRIPT_DIR/devbricksx-cli/install.sh" "$@"
