plugins {
    kotlin("jvm") version "2.3.10"
    application
}

group = "cn.dailystudio"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.github.ajalt.clikt:clikt:4.4.0")
    implementation(files("libs/vector-drawable-tool.jar"))
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.dailystudio.devbricksx.cli.MainKt")
    applicationName = "devbricksx"
}

kotlin {
    jvmToolchain(21)
}

val packageTemplate = tasks.register<Zip>("packageTemplate") {
    archiveFileName.set("template.zip")
    destinationDirectory.set(layout.buildDirectory.dir("generated/resources"))
    from(file("template")) {
        exclude(".gradle/**", "**/build/**", "**/.cxx/**", ".idea/**", "**/*.iml", "local.properties")
    }
}

sourceSets["main"].resources.srcDir(layout.buildDirectory.dir("generated/resources"))

tasks.named("processResources") {
    dependsOn(packageTemplate)
}

tasks.named<CreateStartScripts>("startScripts") {
    doLast {
        unixScript.let { script ->
            if (script.exists()) {
                val original = script.readText()
                val jreDetection = """
# Locate DevBricksX JRE (bundled or configured)
if [ -z "${'$'}DEVBRICKSX_JAVA_HOME" ]; then
    if [ -d "${'$'}APP_HOME/jre" ]; then
        if [ -x "${'$'}APP_HOME/jre/Contents/Home/bin/java" ]; then
            DEVBRICKSX_JAVA_HOME="${'$'}APP_HOME/jre/Contents/Home"
        elif [ -x "${'$'}APP_HOME/jre/bin/java" ]; then
            DEVBRICKSX_JAVA_HOME="${'$'}APP_HOME/jre"
        fi
    elif [ -d "${'$'}HOME/.devbricksx/jre" ]; then
        if [ -x "${'$'}HOME/.devbricksx/jre/Contents/Home/bin/java" ]; then
            DEVBRICKSX_JAVA_HOME="${'$'}HOME/.devbricksx/jre/Contents/Home"
        elif [ -x "${'$'}HOME/.devbricksx/jre/bin/java" ]; then
            DEVBRICKSX_JAVA_HOME="${'$'}HOME/.devbricksx/jre"
        fi
    fi
fi
if [ -n "${'$'}DEVBRICKSX_JAVA_HOME" ]; then
    JAVA_HOME="${'$'}DEVBRICKSX_JAVA_HOME"
fi
"""
                val patched = original.replace(
                    "# Determine the Java command to use to start the JVM.",
                    "$jreDetection\n# Determine the Java command to use to start the JVM."
                )
                script.writeText(patched)
            }
        }
        windowsScript.let { script ->
            if (script.exists()) {
                val original = script.readText()
                val jreDetectionWin = """
@rem Locate DevBricksX JRE (bundled or configured)
if not defined DEVBRICKSX_JAVA_HOME (
    if exist "%APP_HOME%\jre\bin\java.exe" set DEVBRICKSX_JAVA_HOME=%APP_HOME%\jre
    if not defined DEVBRICKSX_JAVA_HOME if exist "%USERPROFILE%\.devbricksx\jre\bin\java.exe" set DEVBRICKSX_JAVA_HOME=%USERPROFILE%\.devbricksx\jre
)
if defined DEVBRICKSX_JAVA_HOME set JAVA_HOME=%DEVBRICKSX_JAVA_HOME%
"""
                val patched = original.replace(
                    "@rem Find java.exe",
                    "$jreDetectionWin\n@rem Find java.exe"
                )
                script.writeText(patched)
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
