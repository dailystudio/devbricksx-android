package com.dailystudio.devbricksx.ksp

import com.dailystudio.devbricksx.ksp.processors.ViewsProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class ViewsProcessorProvider: SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ViewsProcessor(environment)
    }

}