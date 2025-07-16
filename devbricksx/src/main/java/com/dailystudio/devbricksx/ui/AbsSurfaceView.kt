package com.dailystudio.devbricksx.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.Choreographer
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.dailystudio.devbricksx.development.Logger

abstract class AbsSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : SurfaceView(context, attrs, defStyleAttr, defStyleRes), SurfaceHolder.Callback, Choreographer.FrameCallback {

    private var surfaceReady = false
    private var isDrawingActive = false

    // --- Start of New Code for Frame Rate Control ---

    // The time in nanoseconds between each frame. 1 second = 1,000,000,000 nanoseconds.
    private var frameIntervalNanos: Long = 1_000_000_000L / 60 // Default to 60 FPS

    // The timestamp of the last frame that was actually drawn.
    private var lastFrameTimeNanos: Long = 0

    // --- End of New Code ---

    init {
        holder.addCallback(this)
        holder.setFormat(PixelFormat.TRANSLUCENT)
        setZOrderOnTop(true)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceReady = true
        startDrawing()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceReady = false
        stopDrawing()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun doFrame(frameTimeNanos: Long) {
        if (!isDrawingActive) {
            return
        }

        // --- Start of Modified Logic ---

        val elapsedNanos = frameTimeNanos - lastFrameTimeNanos

        // Check if enough time has passed to draw the next frame.
        if (elapsedNanos < frameIntervalNanos) {
            // Not time yet, just request the next callback and skip drawing.
            Choreographer.getInstance().postFrameCallback(this)
            return
        }

        // It's time to draw. Update the last frame time.
        // We use a modulo operation to prevent lastFrameTimeNanos from growing indefinitely
        // while still preserving the correct interval.
        lastFrameTimeNanos = frameTimeNanos - (elapsedNanos % frameIntervalNanos)

        // --- End of Modified Logic ---


        var canvas: Canvas? = null
        try {
            canvas = holder.lockCanvas()
            if (canvas != null) {
                synchronized(holder) {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    drawingCanvas(canvas)
                }
            }
        } catch (e: Exception) {
            Logger.error("Error during rendering: $e")
        } finally {
            if (canvas != null) {
                try {
                    holder.unlockCanvasAndPost(canvas)
                } catch (e: Exception) {
                    Logger.error("Failed to unlock canvas: $e")
                }
            }
        }

        // Request the next frame callback to keep the loop running.
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun startDrawing() {
        if (!surfaceReady) {
            Logger.warn("Surface not ready, abort starting drawing.")
            return
        }
        if (isDrawingActive) {
            Logger.debug("Drawing is already active.")
            return
        }

        isDrawingActive = true
        // Reset last frame time when starting
        lastFrameTimeNanos = 0
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun stopDrawing() {
        if (!isDrawingActive) {
            return
        }

        isDrawingActive = false
        Choreographer.getInstance().removeFrameCallback(this)
    }

    // --- New Public Method to Set FPS ---
    /**
     * Sets the desired frame rate for the drawing loop.
     * @param fps The target frames per second. Must be greater than 0.
     */
    fun setFrameRate(fps: Int) {
        if (fps <= 0) {
            Logger.warn("Frame rate must be positive. Ignoring.")
            return
        }
        frameIntervalNanos = 1_000_000_000L / fps
    }


    abstract fun drawingCanvas(canvas: Canvas)

}