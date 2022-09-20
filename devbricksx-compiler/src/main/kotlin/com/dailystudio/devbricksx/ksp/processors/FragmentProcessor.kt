package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.processors.step.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.*
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class FragmentProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            ListFragmentStep(this),
        )

}