# DevBricksX CLI (`devbricksx`)

A command-line interface tool written in Kotlin to quickly create and bootstrap new Android projects utilizing the DevBricksX libraries and architectural components.

## Features

- **Project Generation**: Scaffold a multi-module Android project ready for development using DevBricksX.
- **Template-Based**: Bundles the official DevBricksX Android template inside the CLI distribution.
- **Developer-Friendly Template**: The template project inside `template/` is an actual, testable Android project aligned with the root `devbricksx-android` project configuration (Gradle wrapper 9.7, AGP 9.3, Kotlin 2.3.10, SDK 37, JDK 21). You can open and test it directly in Android Studio.
- **Flexible UI Targets**:
  - `all` (default): Scaffolds both Android Views (XML) and Jetpack Compose app modules.
  - `views`: Android Views (XML) only.
  - `compose`: Jetpack Compose only.
- **Modular Components**:
  - Optional `ndk` extra module (`core-native` module with CMake and JNI glue).
- **Theme & Branding**:
  - Customize application theme primary color (`--theme-color`).
  - Automatic package renaming and source code alignment.

## Building and Installing

### Build Distribution
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
- `-u`, `--ui <text>`: UI target: `all`, `views`, `compose` (default: `all`).
- `-e`, `--extra-modules <text>`: Extra modules to include, comma-separated (e.g. `ndk`).
- `-t`, `--theme-color <text>`: Primary theme color hex (e.g. `#008577` or `#3F51B5`).
- `-h`, `--help`: Show command help.

#### Examples

1. **Create full project (Views + Compose + NDK)**:
```bash
devbricksx create -n "My App" -p "com.example.myapp" -o ./MyApp -u all -e ndk -t "#3F51B5"
```

2. **Create Jetpack Compose-only project**:
```bash
devbricksx create -n "Compose App" -p "com.example.composeapp" -u compose
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
