package com.dailystudio.devbricksx.cli

import com.github.ajalt.clikt.core.subcommands
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DevBricksXCommandTest {

    @Test
    fun `test cli help output`() {
        val cmd = DevBricksXCommand().subcommands(CreateCommand())
        val helpText = cmd.getFormattedHelp()!!
        assertTrue(helpText.contains("devbricksx"))
        assertTrue(helpText.contains("create"))
    }

    @Test
    fun `test create command help output`() {
        val createCmd = CreateCommand()
        val helpText = createCmd.getFormattedHelp()!!
        assertTrue(helpText.contains("--name"))
        assertTrue(helpText.contains("--package"))
        assertTrue(helpText.contains("--ui"))
        assertTrue(helpText.contains("--output"))
        assertTrue(helpText.contains("--extra-modules"))
        assertTrue(helpText.contains("--theme-color"))
    }

    @Test
    fun `test create command execution via main args`(@TempDir tempDir: File) {
        val outDir = File(tempDir, "CliTestApp")
        val cmd = DevBricksXCommand().subcommands(CreateCommand())

        cmd.parse(
            listOf(
                "create",
                "-n", "Cli Test App",
                "-p", "com.example.clitest",
                "-o", outDir.absolutePath,
                "-u", "compose",
                "-t", "#2196F3"
            )
        )

        assertTrue(outDir.exists())
        assertTrue(File(outDir, "app-compose").exists())
        assertTrue(File(outDir, "core").exists())
        assertTrue(File(outDir, "app-compose/src/main/java/com/example/clitest/compose/MainActivity.kt").exists())
    }
}
