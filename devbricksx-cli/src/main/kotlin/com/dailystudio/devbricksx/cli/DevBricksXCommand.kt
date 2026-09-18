package com.dailystudio.devbricksx.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.versionOption

class DevBricksXCommand : CliktCommand(
    name = "devbricksx",
    help = "DevBricksX CLI - Command-line tool for creating and managing Android projects with DevBricksX."
) {
    init {
        versionOption("1.0.0")
    }

    override fun run() = Unit
}
