package com.dailystudio.devbricksx.async

import com.dailystudio.devbricksx.development.Logger

/**
 * An abstract base class for managing a background thread.
 *
 * It provides methods to start and stop the thread safely, and a check for its running state.
 */
abstract class ManagedThread() {

    private var thread: Thread? = null

    private var isRunning: Boolean = false

    /**
     * Starts the background thread if it's not already running.
     */
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

    /**
     * Stops the background thread.
     */
    @Synchronized
    fun stop() {
        if (thread == null) {
            return
        }

        isRunning = false

        Logger.debug("thread is stopped: $thread")

        thread = null
    }

    /**
     * Checks if the thread is currently running.
     *
     * @return True if running, false otherwise.
     */
    @Synchronized
    protected fun isRunning(): Boolean {
        return isRunning
    }

    /**
     * The method to be executed in the background thread.
     * Implement this method to perform background tasks.
     */
    abstract fun runInBackground()

}