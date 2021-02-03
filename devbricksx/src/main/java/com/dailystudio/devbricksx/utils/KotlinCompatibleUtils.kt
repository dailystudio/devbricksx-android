package com.dailystudio.devbricksx.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

object KotlinCompatibleUtils {

    fun <T, R> mapFlow(flow: Flow<T>, transform: suspend (value: T) -> R) : Flow<R> {
        return flow.map(transform)
    }

}
