package com.dailystudio.devbricksx.samples.imagematrix

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.*
import com.dailystudio.devbricksx.development.Logger
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class DrawingPad: SurfaceView, SurfaceHolder.Callback {

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

    private var hasPendingRenderRequest = false

    private var bitmap: Bitmap? = null

    private val tracks: MutableList<MutableList<PointF>> = mutableListOf()

    init {
        holder.addCallback(this)

        setWillNotDraw(false)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Logger.debug("GameView surface is created")
        stopDrawThread()

        surfaceReady = true
        startDrawThread()

        if (hasPendingRenderRequest) {
            renderFrame()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        Logger.debug("GameView surface is destroyed")
        // Surface is not used anymore - stop the drawing thread
        stopDrawThread()
        // and release the surface
        holder?.surface?.release()

        surfaceReady = false
        Logger.debug("Destroyed")
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    private fun startDrawThread() {
        if (surfaceReady) {
            drawingService = Executors.newSingleThreadExecutor()
            drawingActive = true
        }
    }

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

    fun setImage(bitmap: Bitmap) {
        this.bitmap = bitmap

        requestLayout()
        requestRendering()
    }

    private fun requestRendering() {
        if (drawingService == null) {
            Logger.debug("surface is not ready, skip and try it later")

            hasPendingRenderRequest = true
        } else {
            drawingService?.execute {
                renderFrame()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val point = PointF(event.x, event.y)
            Logger.debug("new point: $point")

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val track = mutableListOf<PointF>().apply {
                        add(point)
                    }

                    tracks.add(track)
                }

                else -> {
                    val track = tracks.last()

                    track.add(point)
                }

            }

            requestRendering()
        }

        return true
    }

    private fun renderFrame() {
        var canvas: Canvas? = null
        try {
            canvas = holder.lockCanvas(null)
            Logger.debug("render frame on canvas: $canvas")

            if (canvas == null) {
                Logger.debug("surface is not ready, skip and try it later")
                hasPendingRenderRequest = true
            }

            synchronized (holder) {
                canvas?.let {
                    drawingCanvas(it)
                }
            }
        } finally {
            canvas?.let {
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun drawingCanvas(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
            strokeWidth = 2f
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, paint)
        }

        for (t in tracks) {
            for (i in 0 until (t.size - 1)) {
                canvas.drawLine(
                        t[i].x, t[i].y,
                        t[i + 1].x, t[i + 1].y,
                        paint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (bitmap == null) {
            return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

        val wSpec = MeasureSpec.makeMeasureSpec(bitmap!!.width, MeasureSpec.EXACTLY)
        val hSpec = MeasureSpec.makeMeasureSpec(bitmap!!.height, MeasureSpec.EXACTLY)

        super.onMeasure(wSpec, hSpec)
    }

}