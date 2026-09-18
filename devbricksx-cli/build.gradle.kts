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

tasks.test {
    useJUnitPlatform()
}
