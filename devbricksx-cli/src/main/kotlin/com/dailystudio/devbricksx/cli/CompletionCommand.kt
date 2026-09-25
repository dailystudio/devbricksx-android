package com.dailystudio.devbricksx.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintCompletionMessage
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.choice

class CompletionCommand(
    name: String = "completion",
    help: String = "Generate a tab-complete script for the given shell",
    hidden: Boolean = true
) : CliktCommand(name = name, help = help, hidden = hidden) {

    val shell by argument(help = "The shell to generate completion for")
        .choice("bash", "zsh", "fish")

    override fun run() {
        val rootCommand = currentContext.parent?.command ?: this
        val script = generateCompletion(rootCommand, shell)
        throw PrintCompletionMessage(script)
    }

    companion object {
        fun generateCompletion(command: CliktCommand, shell: String): String {
            val normalizedShell = shell.trim().lowercase()
            return when (normalizedShell) {
                "fish" -> patchFishCompletion(generateFishCompletion(command))
                "zsh" -> patchZshCompletion(generateBashOrZshCompletion(command, zsh = true))
                else -> patchBashCompletion(generateBashOrZshCompletion(command, zsh = false))
            }
        }

        private fun generateBashOrZshCompletion(command: CliktCommand, zsh: Boolean): String {
            val clazz = Class.forName("com.github.ajalt.clikt.completion.BashCompletionGenerator")
            val instance = clazz.getField("INSTANCE").get(null)
            val method = clazz.getMethod("generateBashOrZshCompletion", CliktCommand::class.java, java.lang.Boolean.TYPE)
            return method.invoke(instance, command, zsh) as String
        }

        private fun generateFishCompletion(command: CliktCommand): String {
            val clazz = Class.forName("com.github.ajalt.clikt.completion.FishCompletionGenerator")
            val instance = clazz.getField("INSTANCE").get(null)
            val method = clazz.getMethod("generateFishCompletion", CliktCommand::class.java)
            return method.invoke(instance, command) as String
        }

        fun patchBashCompletion(script: String): String {
            return hideSubcommandFromBashOrZsh(script, "completion")
        }

        fun patchFishCompletion(script: String): String {
            var patched = script.replace(Regex("""set -l (\w+)_subcommands '([^']*)'""")) { match ->
                val varName = match.groupValues[1]
                val words = match.groupValues[2].split("\\s+".toRegex()).filter { it != "completion" }
                "set -l ${varName}_subcommands '${words.joinToString(" ")}'"
            }
            // Remove the subcommand completion definition so it's not offered as a choice
            patched = patched.replace(Regex("""(?m)^complete -c \w+ -f -n __fish_use_subcommand -a completion .*$\n?"""), "")
            return patched
        }

        fun patchZshCompletion(script: String): String {
            val guardedInit = """
# Initialize completion system only if not already initialized
if ! type compdef >/dev/null 2>&1; then
    autoload -Uz compinit && compinit
fi
if ! type complete >/dev/null 2>&1; then
    autoload -Uz bashcompinit && bashcompinit
fi
""".trim()

            // 1. Replace unconditional compinit and bashcompinit which wipes out existing completions in _comps
            var patched = script.replace(
                "autoload -Uz compinit\ncompinit\nautoload -Uz bashcompinit\nbashcompinit",
                guardedInit
            )

            // 2. Isolate __skip_opt_eq to avoid colliding with other Clikt-based CLI tools (e.g. primer-cli)
            patched = patched.replace("__skip_opt_eq", "__devbricksx_skip_opt_eq")

            // 3. Ensure all completion functions enable KSH_ARRAYS for 0-indexed bash compatibility under zsh
            val regex = Regex("(?m)^(_devbricksx[a-zA-Z0-9_]*\\(\\) \\{)")
            patched = regex.replace(patched) { matchResult ->
                "${matchResult.groupValues[1]}\n  [[ -n \"\$ZSH_VERSION\" ]] && setopt localoptions KSH_ARRAYS"
            }

            // 4. Hide completion command from completion choices
            patched = hideSubcommandFromBashOrZsh(patched, "completion")

            return patched
        }

        private fun hideSubcommandFromBashOrZsh(script: String, subcommandName: String): String {
            return script.replace(Regex("""compgen -W '([^']*)'""")) { match ->
                val words = match.groupValues[1].split("\\s+".toRegex()).filter { it != subcommandName }
                "compgen -W '${words.joinToString(" ")}'"
            }
        }
    }
}
