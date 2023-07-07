package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.processors.step.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.compose.ComposeScreenStep
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class ComposeProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            ComposeScreenStep(this),
        )

}