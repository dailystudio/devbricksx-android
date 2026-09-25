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
        val cmd = DevBricksXCommand().subcommands(
            CreateCommand(),
            CompletionCommand(name = "completion")
        )
        val helpText = cmd.getFormattedHelp()!!
        assertTrue(helpText.contains("devbricksx"))
        assertTrue(helpText.contains("create"))
        // completion should be hidden from help output
        assertTrue(!helpText.contains("completion"))
    }

    @Test
    fun `test zsh completion script is safe and guarded`() {
        val cmd = DevBricksXCommand().subcommands(
            CreateCommand(),
            CompletionCommand(name = "completion")
        )
        val zshScript = try {
            cmd.parse(listOf("completion", "zsh"))
            ""
        } catch (e: com.github.ajalt.clikt.core.PrintCompletionMessage) {
            e.message ?: ""
        }

        // Must not have bare unconditional compinit which clears _comps
        assertTrue(!zshScript.contains("\ncompinit\nautoload"))
        // Must contain guarded checks
        assertTrue(zshScript.contains("if ! type compdef >/dev/null 2>&1; then"))
        assertTrue(zshScript.contains("if ! type complete >/dev/null 2>&1; then"))
        // Must contain KSH_ARRAYS for 0-indexed bash completion compatibility
        assertTrue(zshScript.contains("setopt localoptions KSH_ARRAYS"))
        // Must use isolated helper function to avoid namespace collision
        assertTrue(zshScript.contains("__devbricksx_skip_opt_eq"))
        assertTrue(!zshScript.contains("__skip_opt_eq()"))
        // Must not offer completion as a subcommand choice
        assertTrue(!zshScript.contains("create completion"))
        assertTrue(zshScript.contains("compgen -W 'create'"))
    }

    @Test
    fun `test bash and fish completion scripts`() {
        val bashCmd = DevBricksXCommand().subcommands(
            CreateCommand(),
            CompletionCommand(name = "completion")
        )
        val bashScript = try {
            bashCmd.parse(listOf("completion", "bash"))
            ""
        } catch (e: com.github.ajalt.clikt.core.PrintCompletionMessage) {
            e.message ?: ""
        }
        assertTrue(bashScript.contains("complete -F _devbricksx devbricksx"))
        assertTrue(!bashScript.contains("create completion"))
        assertTrue(bashScript.contains("compgen -W 'create'"))

        val fishCmd = DevBricksXCommand().subcommands(
            CreateCommand(),
            CompletionCommand(name = "completion")
        )
        val fishScript = try {
            fishCmd.parse(listOf("completion", "fish"))
            ""
        } catch (e: com.github.ajalt.clikt.core.PrintCompletionMessage) {
            e.message ?: ""
        }
        assertTrue(fishScript.contains("complete -c devbricksx"))
        assertTrue(fishScript.contains("set -l devbricksx_subcommands 'create'"))
        assertTrue(!fishScript.contains("set -l devbricksx_subcommands 'create completion'"))
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
