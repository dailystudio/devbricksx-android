package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.processors.step.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.data.*
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