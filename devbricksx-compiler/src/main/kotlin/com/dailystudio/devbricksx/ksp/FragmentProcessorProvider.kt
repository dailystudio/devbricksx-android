package com.dailystudio.devbricksx.ksp

import com.dailystudio.devbricksx.ksp.processors.FragmentProcessor
import com.dailystudio.devbricksx.ksp.processors.ViewProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class FragmentProcessorProvider: SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return FragmentProcessor(environment)
    }

}