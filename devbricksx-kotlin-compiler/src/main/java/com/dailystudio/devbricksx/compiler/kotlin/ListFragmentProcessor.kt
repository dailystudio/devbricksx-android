package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.annotations.ListFragment
import com.google.auto.service.AutoService
import javax.annotation.processing.Processor

@AutoService(Processor::class)
class ListFragmentProcessor : BaseListFragmentProcessor() {

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ListFragment::class.java.name)
    }

}