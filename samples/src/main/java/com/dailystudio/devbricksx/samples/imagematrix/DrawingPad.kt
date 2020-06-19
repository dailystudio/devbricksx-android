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

interface OnTracksChangedListener {

    fun onTracksChanged(pad: DrawingPad, tracks: List<List<PointF>>)

}

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
    private var listener: OnTracksChangedListener? = null

    private var enableTracksEditing:Boolean = false

    init {
        holder.addCallback(this)

        setWillNotDraw(false)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        stopDrawThread()

        surfaceReady = true
        startDrawThread()

        if (hasPendingRenderRequest) {
            renderFrame()
        }
    }


    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopDrawThread()
        holder?.surface?.release()

        surfaceReady = false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
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

    fun setTracks(newTracks: List<List<PointF>>?) {
        tracks.clear()

        newTracks?.let {
            for(track in newTracks) {
                val trackCopy = mutableListOf<PointF>()

                tracks.add(trackCopy)
                for (p in track) {
                    trackCopy.add(p)
                }
            }
        }

        requestLayout()
        requestRendering()
    }

    fun setTracksEditing(enabled: Boolean) {
        enableTracksEditing = enabled
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
        if (!enableTracksEditing) {
            return super.onTouchEvent(event)
        }

        event?.let {
            val point = PointF(event.x, event.y)

            parent.requestDisallowInterceptTouchEvent(true);

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val track = mutableListOf<PointF>().apply {
                        add(point)
                    }

                    synchronized(tracks) {
                        tracks.add(track)
                    }
                }
                else -> {
                    synchronized(tracks) {
                        val track = tracks.last()

                        track.add(point)

                        if (event.action == MotionEvent.ACTION_UP
                                || event.action == MotionEvent.ACTION_CANCEL) {
                            notifyTracksChanged()
                        }
                    }
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

        for ((tn, t) in tracks.withIndex()) {
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

    private fun getTracks(): List<List<PointF>> {
        synchronized(tracks) {
            val list = mutableListOf<List<PointF>>()

            for (track in tracks) {
                val t = mutableListOf<PointF>()

                list.add(t)
                for (p in track) {
                    t.add(p)
                }
            }

            return list
        }
    }

    fun setOnTracksChangedListener(l: OnTracksChangedListener) {
        listener = l
    }

    private fun notifyTracksChanged() {
        removeCallbacks(notifyRunnable)
        post(notifyRunnable)
    }

    private val notifyRunnable = Runnable {
        listener?.onTracksChanged(this@DrawingPad, getTracks())
    }
}