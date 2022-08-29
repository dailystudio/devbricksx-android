package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.*
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class RoomCompanionProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            RoomCompanionStep(this),
            RoomCompanionDatabaseStep(this),
            RoomCompanionDaoStep(this),
            DaoExtensionStep(this),
            RoomCompanionRepositoryStep(this),
        )

}