package com.dailystudio.devbricksx.ksp

import com.dailystudio.devbricksx.ksp.processors.ComposeProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class ComposeProcessorProvider: SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ComposeProcessor(environment)
    }

}