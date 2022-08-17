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
            step.runStep(resolver)?.let {
                for (r in it) {
                    writeToFile(r)
                }
            }.also {
                warn("results = $it")
            }
        }

        return emptyList()
    }


    protected abstract val steps: Array<ProcessStep>

}