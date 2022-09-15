package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.annotations.view.DiffUtil
import com.dailystudio.devbricksx.ksp.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.*
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class ViewsProcessor(
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