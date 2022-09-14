package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.AbsDiffUtilStep
import com.dailystudio.devbricksx.ksp.processors.step.InMemoryCompanionDiffUtilStep
import com.dailystudio.devbricksx.ksp.processors.step.RoomCompanionDiffUtilStep
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class ViewsProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            RoomCompanionDiffUtilStep(this),
            InMemoryCompanionDiffUtilStep(this),
        )

}