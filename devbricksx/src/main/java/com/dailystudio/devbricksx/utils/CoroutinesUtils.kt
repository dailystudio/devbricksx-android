package com.dailystudio.devbricksx.utils

import kotlinx.coroutines.*

object CoroutinesUtils {

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