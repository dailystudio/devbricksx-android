package com.dailystudio.devbricksx.ksp

import com.dailystudio.devbricksx.ksp.processors.DataStoreCompanionProcessor
import com.dailystudio.devbricksx.ksp.processors.InMemoryCompanionProcessor
import com.dailystudio.devbricksx.ksp.processors.RoomCompanionProcessor
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class DataStoreCompanionProcessorProvider: SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DataStoreCompanionProcessor(environment)
    }

}