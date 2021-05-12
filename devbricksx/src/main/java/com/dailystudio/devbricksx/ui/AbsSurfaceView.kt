package com.dailystudio.devbricksx.ui

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Process
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.dailystudio.devbricksx.development.Logger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

abstract class AbsSurfaceView: SurfaceView, SurfaceHolder.Callback {

    private var framesPerSecond = 60

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private var drawingService: ExecutorService? = null
    private var surfaceReady = false
    private var drawingActive = false

    init {
        holder.addCallback(this)
        holder.setFormat(PixelFormat.RGBA_8888)

        setZOrderOnTop(true)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        stopDrawThread()

        surfaceReady = true
        startDrawThread()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopDrawThread()
        holder.surface?.release()

        surfaceReady = false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    @Synchronized
    private fun startDrawThread() {
        if (surfaceReady) {
            drawingService = Executors.newSingleThreadExecutor().apply {
                drawingActive = true

                execute {
                    /*
                     * MUST HAVE: to avoid drawing thread affect the main thread performance
                     */
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
                    renderFrames()
                }
            }
        }
    }

    @Synchronized
    private fun stopDrawThread() {
        if (drawingService == null) {
            Logger.debug("drawing service is null, needn't to stop")
            return
        }

        drawingActive = false

        drawingService?.let {
            try {
                Logger.debug("shutting down drawing service ...")
                it.shutdown()
            } catch (e: Exception) {
                Logger.warn("Could not shut down drawing service")
            }
        }

        drawingService = null
    }

    fun setFramesPerSecond(fps: Int) {
        framesPerSecond = fps
    }

    private fun renderFrames() {
        var canvas: Canvas? = null

        while (true) {

            synchronized(drawingActive) {
                if (!drawingActive) {
                    return
                }
            }

            val startTime = System.currentTimeMillis()
            try {
                canvas = holder.lockCanvas(null)

                if (canvas == null) {
                    Logger.debug("surface is not ready, skip and try it later")
                }

                synchronized(holder) {
                    canvas?.let {
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                        drawingCanvas(it)
                    }
                }
            } finally {
                canvas?.let {
                    holder.unlockCanvasAndPost(canvas)
                }
            }

            val endTime = System.currentTimeMillis()

            val drawTime = (endTime - startTime)
            val frameTime = (1000 / framesPerSecond)
            val sleepTime = (frameTime - drawTime)

            if (sleepTime > 0) {
//                Logger.debug("sleep with remained frame time = $sleepTime [expected: $frameTime, elapsed: $drawTime]")

                try {
                    Thread.sleep(frameTime - drawTime)
                } catch (e: InterruptedException) {
                }
            }
        }
    }

    abstract fun drawingCanvas(canvas: Canvas)

}