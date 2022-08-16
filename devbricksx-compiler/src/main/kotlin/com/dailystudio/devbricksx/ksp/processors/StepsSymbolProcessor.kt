package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.ProcessStep
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated

abstract class StepsSymbolProcessor (environment: SymbolProcessorEnvironment)
    : BaseSymbolProcessor(environment) {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        warn("kotlin version: ${KotlinVersion.CURRENT}")

        for (step in steps) {
            val results = step.runStep(resolver)

            for (r in results) {
                writeToFile(r)
            }
        }

        return emptyList()
    }


    protected abstract val steps: Array<ProcessStep>

}