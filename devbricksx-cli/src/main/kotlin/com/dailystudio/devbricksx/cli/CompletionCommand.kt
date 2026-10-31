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
        private const val BASH_COMPLETION_HELPERS = """
__devbricksx_complete_dirs() {
  local word="${'$'}1"
  [[ "${'$'}word" == "~" ]] && word="~/"
  local IFS=${'$'}'\n'
  COMPREPLY=(${'$'}(compgen -d -- "${'$'}word"))
}

__devbricksx_complete_files() {
  local word="${'$'}1"
  [[ "${'$'}word" == "~" ]] && word="~/"
  local IFS=${'$'}'\n'
  COMPREPLY=(${'$'}(compgen -f -- "${'$'}word"))
}
"""

        private const val ZSH_COMPLETION_HELPERS = """
__devbricksx_complete_dirs() {
  [[ -n "${'$'}ZSH_VERSION" ]] && setopt localoptions glob bareglobqual nullglob
  local word="${'$'}1"
  [[ "${'$'}word" == "~" ]] && word="~/"
  if [[ "${'$'}word" == "~"* ]]; then
    local expanded="${'$'}{word/#\~/${'$'}HOME}"
    local -a matches=(${'$'}{expanded}*(N-/))
    if [[ "${'$'}word" == *"."* || "${'$'}word" == *"/" ]]; then
      matches+=(${'$'}{expanded}.*(N-/))
    fi
    COMPREPLY=()
    local m
    for m in "${'$'}{matches[@]}"; do
      local base="${'$'}{m##*/}"
      [[ "${'$'}base" == "." || "${'$'}base" == ".." ]] && continue
      COMPREPLY+=("${'$'}{m/#${'$'}HOME/~}")
    done
  else
    local -a matches=(${'$'}{word}*(N-/))
    if [[ "${'$'}word" == *"."* ]]; then
      matches+=(${'$'}{word}.*(N-/))
    fi
    COMPREPLY=()
    local m
    for m in "${'$'}{matches[@]}"; do
      local base="${'$'}{m##*/}"
      [[ "${'$'}base" == "." || "${'$'}base" == ".." ]] && continue
      COMPREPLY+=("${'$'}m")
    done
  fi
}

__devbricksx_complete_files() {
  [[ -n "${'$'}ZSH_VERSION" ]] && setopt localoptions glob bareglobqual nullglob
  local word="${'$'}1"
  [[ "${'$'}word" == "~" ]] && word="~/"
  if [[ "${'$'}word" == "~"* ]]; then
    local expanded="${'$'}{word/#\~/${'$'}HOME}"
    local -a matches=(${'$'}{expanded}*(N))
    if [[ "${'$'}word" == *"."* || "${'$'}word" == *"/" ]]; then
      matches+=(${'$'}{expanded}.*(N))
    fi
    COMPREPLY=()
    local m
    for m in "${'$'}{matches[@]}"; do
      local base="${'$'}{m##*/}"
      [[ "${'$'}base" == "." || "${'$'}base" == ".." ]] && continue
      COMPREPLY+=("${'$'}{m/#${'$'}HOME/~}")
    done
  else
    local -a matches=(${'$'}{word}*(N))
    if [[ "${'$'}word" == *"."* ]]; then
      matches+=(${'$'}{word}.*(N))
    fi
    COMPREPLY=()
    local m
    for m in "${'$'}{matches[@]}"; do
      local base="${'$'}{m##*/}"
      [[ "${'$'}base" == "." || "${'$'}base" == ".." ]] && continue
      COMPREPLY+=("${'$'}m")
    done
  fi
}
"""

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
            var patched = hideSubcommandFromBashOrZsh(script, "completion")
            patched = insertHelpers(patched, BASH_COMPLETION_HELPERS)
            patched = patchDirectoryCompletion(patched)
            patched = patchFileCompletion(patched)
            patched = patched.replace("complete -F _devbricksx devbricksx", "complete -o filenames -F _devbricksx devbricksx")
            return patched
        }

        fun patchFishCompletion(script: String): String {
            var patched = script.replace(Regex("""set -l (\w+)_subcommands '([^']*)'""")) { match ->
                val varName = match.groupValues[1]
                val words = match.groupValues[2].split("\\s+".toRegex()).filter { it != "completion" }
                "set -l ${varName}_subcommands '${words.joinToString(" ")}'"
            }
            // Remove the subcommand completion definition so it's not offered as a choice
            patched = patched.replace(Regex("""(?m)^complete -c \w+ -f -n __fish_use_subcommand -a completion .*$\n?"""), "")

            // Complete directories for create <dir> and open <dir>
            patched = patched.replace(
                Regex("""(?m)^(complete -c devbricksx -n "__fish_seen_subcommand_from (?:create|open)" )-[Ff]( -d '.*(?:directory|project).*')"""),
                "$1-f -a \"(__fish_complete_directories)\"$2"
            )

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

            // 5. Insert helper functions
            patched = insertHelpers(patched, ZSH_COMPLETION_HELPERS)

            // 6. Enhance directory completion for 'dir' argument (including ~ support)
            patched = patchDirectoryCompletion(patched)

            // 7. Enhance file/path completion for options and arguments (including ~ support)
            patched = patchFileCompletion(patched)

            // 8. Enable filenames mode for trailing slashes and proper escaping
            patched = patched.replace("complete -F _devbricksx devbricksx", "complete -o filenames -F _devbricksx devbricksx")

            return patched
        }

        private fun insertHelpers(script: String, helpers: String): String {
            return if (script.contains("__devbricksx_skip_opt_eq() {")) {
                script.replace("__devbricksx_skip_opt_eq() {", "$helpers\n__devbricksx_skip_opt_eq() {")
            } else if (script.contains("__skip_opt_eq() {")) {
                script.replace("__skip_opt_eq() {", "$helpers\n__skip_opt_eq() {")
            } else {
                "$helpers\n$script"
            }
        }

        private fun patchDirectoryCompletion(script: String): String {
            val dirRegex = Regex("""(?m)(dir\)\s*\n\s*)COMPREPLY=\(\$\(compgen -o default -- "\$\{word\}"\)\)""")
            return dirRegex.replace(script) { match ->
                "${match.groupValues[1]}__devbricksx_complete_dirs \"\${word}\""
            }
        }

        private fun patchFileCompletion(script: String): String {
            val defaultCompgen = Regex("""(?m)COMPREPLY=\(\$\(compgen -o default -- "\$\{word\}"\)\)""")
            return defaultCompgen.replace(script) { "__devbricksx_complete_files \"\${word}\"" }
        }

        private fun hideSubcommandFromBashOrZsh(script: String, subcommandName: String): String {
            return script.replace(Regex("""compgen -W '([^']*)'""")) { match ->
                val words = match.groupValues[1].split("\\s+".toRegex()).filter { it != subcommandName }
                "compgen -W '${words.joinToString(" ")}'"
            }
        }
    }
}
