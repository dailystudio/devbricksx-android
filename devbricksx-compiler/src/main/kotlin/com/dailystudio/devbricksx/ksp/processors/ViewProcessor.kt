package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.processors.step.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.*
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class ViewProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            DefaultDiffUtilStep(this),
            RoomCompanionDiffUtilStep(this),
            InMemoryCompanionDiffUtilStep(this),
            AdapterStep(this),
        )

}