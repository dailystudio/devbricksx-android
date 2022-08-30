package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.InMemoryCompanionStep
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class InMemoryCompanionProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            InMemoryCompanionStep(this)
        )

}