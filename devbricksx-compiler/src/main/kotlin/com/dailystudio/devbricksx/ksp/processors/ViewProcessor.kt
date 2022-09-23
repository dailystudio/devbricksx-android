package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.processors.step.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.view.RoomCompanionDiffUtilStep
import com.dailystudio.devbricksx.ksp.processors.step.view.AdapterStep
import com.dailystudio.devbricksx.ksp.processors.step.view.DefaultDiffUtilStep
import com.dailystudio.devbricksx.ksp.processors.step.view.FragmentAdapterStep
import com.dailystudio.devbricksx.ksp.processors.step.view.InMemoryCompanionDiffUtilStep
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
            FragmentAdapterStep(this),
        )

}