package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.annotations.view.Adapter
import com.dailystudio.devbricksx.annotations.view.FragmentAdapter
import com.dailystudio.devbricksx.ksp.processors.step.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.view.RoomCompanionDiffUtilStep
import com.dailystudio.devbricksx.ksp.processors.step.view.AdapterStep
import com.dailystudio.devbricksx.ksp.processors.step.view.DefaultDiffUtilStep
import com.dailystudio.devbricksx.ksp.processors.step.view.FragmentAdapterStep
import com.dailystudio.devbricksx.ksp.processors.step.view.GenericDiffUtilStep
import com.dailystudio.devbricksx.ksp.processors.step.view.InMemoryCompanionDiffUtilStep
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class ViewProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            // for classes which are declared in higher library or applications
            GenericDiffUtilStep(Adapter::class, this),
            GenericDiffUtilStep(FragmentAdapter::class, this),

            // for classes which are declared in low-level library
            RoomCompanionDiffUtilStep(this),
            InMemoryCompanionDiffUtilStep(this),
            AdapterStep(this),
            FragmentAdapterStep(this),
        )

}