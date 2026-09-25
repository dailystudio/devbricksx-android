package com.dailystudio.devbricksx.cli

import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) {
    DevBricksXCommand()
        .subcommands(
            CreateCommand(),
            CompletionCommand(name = "completion")
        )
        .main(args)
}
