package com.dailystudio.devbricksx.ksp.processors

import com.dailystudio.devbricksx.ksp.processors.step.ProcessStep
import com.dailystudio.devbricksx.ksp.processors.step.compose.ListScreenStep
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.squareup.kotlinpoet.FileSpec

class ComposeProcessor(
    environment: SymbolProcessorEnvironment
) : StepsSymbolProcessor(environment) {

    override val steps: Array<ProcessStep>
        get() = arrayOf(
            ListScreenStep(this),
        )

    override fun postProcessOnFileBuilder(fileBuilder: FileSpec.Builder) {
        super.postProcessOnFileBuilder(fileBuilder)

        fileBuilder.addImport("androidx.compose.runtime", "getValue")
    }
}