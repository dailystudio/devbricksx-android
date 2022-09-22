package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.processors.step.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.data.DataStoreCompanionStep
import com.dailystudio.devbricksx.ksp.processors.step.data.InMemoryCompanionStep
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class DataStoreCompanionProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            DataStoreCompanionStep(this)
        )

}