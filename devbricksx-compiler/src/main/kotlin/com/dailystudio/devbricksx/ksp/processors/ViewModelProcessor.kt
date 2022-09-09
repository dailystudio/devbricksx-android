package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.ViewModelStep
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class ViewModelProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            ViewModelStep(this)
        )

}