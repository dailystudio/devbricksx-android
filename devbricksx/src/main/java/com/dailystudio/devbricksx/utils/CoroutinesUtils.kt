package com.dailystudio.devbricksx.utils

import kotlinx.coroutines.*

/**
 * Utility class for Coroutines operations.
 */
object CoroutinesUtils {

    /**
     * Creates a debounced function that accepts one parameter.
     *
     * The function will only execute after [waitMs] milliseconds have passed since the last invocation.
     *
     * @param waitMs The debounce delay in milliseconds.
     * @param scope The CoroutineScope to launch the debounce job in.
     * @param func The suspend function to execute.
     * @return A function that accepts parameter T and triggers the debounced execution.
     */
    fun <T> debounce(
        waitMs: Long = 300L,
        scope: CoroutineScope,
        func: suspend (T) -> Unit
    ): (T) -> Unit {
        var debounceJob: Job? = null
        return { param: T ->
            debounceJob?.cancel()
            debounceJob = scope.launch {
                delay(waitMs)
                func(param)
            }
        }
    }

    /**
     * Creates a debounced function that accepts no parameters.
     *
     * The function will only execute after [waitMs] milliseconds have passed since the last invocation.
     *
     * @param waitMs The debounce delay in milliseconds.
     * @param scope The CoroutineScope to launch the debounce job in.
     * @param func The suspend function to execute.
     * @return A function that triggers the debounced execution.
     */
    fun debounce(
        waitMs: Long = 300L,
        scope: CoroutineScope,
        func: suspend () -> Unit
    ): () -> Unit {
        var debounceJob: Job? = null
        return {
            debounceJob?.cancel()
            debounceJob = scope.launch {
                delay(waitMs)
                func()
            }
        }
    }

}