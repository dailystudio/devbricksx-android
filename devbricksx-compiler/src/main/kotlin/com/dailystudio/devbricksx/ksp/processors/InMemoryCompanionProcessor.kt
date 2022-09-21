package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.processors.step.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.data.InMemoryCompanionStep
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class InMemoryCompanionProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            InMemoryCompanionStep(this)
        )

}