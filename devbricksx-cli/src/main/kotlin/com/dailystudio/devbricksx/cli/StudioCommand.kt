package com.dailystudio.devbricksx.cli

import com.github.ajalt.clikt.core.CliktCommand

class StudioCommand : CliktCommand(
    name = "studio",
    help = "Manage and interact with Android Studio."
) {
    override fun run() = Unit
}
