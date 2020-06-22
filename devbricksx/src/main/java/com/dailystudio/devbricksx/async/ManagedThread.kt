package com.dailystudio.devbricksx.async

import com.dailystudio.devbricksx.development.Logger

abstract class ManagedThread() {

    private var thread: Thread? = null

    private var isRunning: Boolean = false

    @Synchronized
    fun start() {
        if (thread != null) {
            return
        }

        thread = Thread(Runnable {
            runInBackground()
        }).also {
            isRunning = true

            Logger.debug("thread is started: $it")

            it.start()
        }
    }

    @Synchronized
    fun stop() {
        if (thread == null) {
            return
        }

        isRunning = false

        Logger.debug("thread is stopped: $thread")

        thread = null
    }

    @Synchronized
    protected fun isRunning(): Boolean {
        return isRunning
    }

    abstract fun runInBackground()

}