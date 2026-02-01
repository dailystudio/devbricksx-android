package com.dailystudio.devbricksx.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext

/**
 * Utility class for Kotlin compatibility operations.
 */
object KotlinCompatibleUtils {

    /**
     * Maps a Flow to another Flow using a transform function.
     *
     * @param flow The source Flow.
     * @param transform The transformation function.
     * @return The transformed Flow.
     */
    fun <T, R> mapFlow(flow: Flow<T>, transform: suspend (value: T) -> R) : Flow<R> {
        return flow.map(transform)
    }

}
