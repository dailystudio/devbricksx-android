package com.dailystudio.devbricksx.music.midi.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.ui.AbsSurfaceView
import com.dailystudio.devbricksx.music.midi.utils.ChannelInfo
import com.dailystudio.devbricksx.music.midi.utils.TrackInfo
import jp.kshoji.javax.sound.midi.MidiEvent
import jp.kshoji.javax.sound.midi.ShortMessage
import jp.kshoji.javax.sound.midi.Track
import kotlin.math.floor
import kotlin.math.max


class MidiChannelViewer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbsSurfaceView(context, attrs, defStyleAttr) {

    companion object {
        const val MINI_BARS = 1
        const val DEFAULT_BARS = 3

        val colors = arrayOf(
            Color.RED,
            Color.parseColor("#FF6600"),
            Color.YELLOW,
            Color.GREEN,
            Color.CYAN,
            Color.BLUE,
            Color.parseColor("#A020F0"),
        )
    }

    private val bgPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
    }

    private val dividerPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }

    private val fgPaint = Paint().apply {
        isAntiAlias = true
        color = Color.CYAN
    }

    private var bars = DEFAULT_BARS
    private var currentTick = -5000L
    private var channelEvents: List<MidiEvent>? = null
    private var channelInfo: ChannelInfo? = null

    fun setBars(bars: Int) {
        this.bars = max(MINI_BARS, bars)
        invalidate()
    }

    override fun drawingCanvas(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        drawDividers(canvas)
        drawNotes(canvas)
    }
    /*
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        offscreenCanvas?.let {
            drawOfflineCanvas(it)
        }

        offscreenBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null);
        }
    }*/

    private fun drawDividers(canvas: Canvas) {
        val dividerWidth = width / bars

        for (i in 1 until bars) {
            val dividerX = i * dividerWidth
            canvas.drawLine(dividerX.toFloat(), 0f, dividerX.toFloat(), height.toFloat(), dividerPaint)
        }
    }

    private fun drawNotes(canvas: Canvas) {
        val events = channelEvents ?: return
        val info = channelInfo ?: return

//        Logger.debug("canvas: ${canvas.width} x ${canvas.height}")

        val ticksRange = 5 * 1000L

        val barRange: Float = info.notes.range.toFloat() / bars
        val barSize: Float = width.toFloat()/ bars
        val tickHeight: Float = height.toFloat()/ ticksRange

//        Logger.debug("bar range: $barRange, bar: $barSize")
//        Logger.debug("tick height: $tickHeight")

        val startTick = currentTick
        val endTick = startTick + ticksRange
//        Logger.debug("ticks: start = $startTick, end = $endTick")

        val noteOnMap = mutableMapOf<Int, Pair<Long, RectF>>()

        val noteRects = mutableListOf<Pair<Long, RectF>>()
        for (i in events.indices) {
            val event = events[i]

            if (event.tick < startTick) {
                continue
            } else if (event.tick > endTick) {
                break
            }

            val message = events[i].message as ShortMessage

            if (message.command != ShortMessage.NOTE_ON
                && message.command != ShortMessage.NOTE_OFF ) {
                continue
            }

            val note = message.data1
            val command = if (message.command == ShortMessage.NOTE_ON) "ON" else "OFF"
            val tick = events[i].tick
            val tickOffset = tick - startTick
            val barIndex = floor((note - info.notes.min) / barRange)

//            Logger.debug("tick[$i]: [$command], note = $note, tick = $tick(offset = $tickOffset), barIndex = $barIndex")

            if (message.command == ShortMessage.NOTE_ON && message.data2 != 0) {
                val rect = RectF(
                    0f + barIndex * barSize,
                    -1f,
                    barSize + barIndex * barSize,
                    height - tickHeight * tickOffset
                )

//                Logger.debug("tick[$i]: create rect for [${command}.$note]: [${rect.left}, ${rect.top}, ${rect.right}, ${rect.bottom}]")

                noteOnMap[note] = Pair(tick, rect)
            } else if (
                message.command == ShortMessage.NOTE_ON && message.data2 == 0 ||
                message.command == ShortMessage.NOTE_OFF) {
                val pair = noteOnMap.remove(note) ?: Pair(tick, RectF(
                    0f + barIndex * barSize,
                    height.toFloat(),
                    barSize + barIndex * barSize,
                    height.toFloat()
                ))

                pair.second.top = height - tickHeight * tickOffset
//                Logger.debug("tick[$i]: create rect for [${command}.$note]: [${pair.second.left}, ${pair.second.top}, ${pair.second.right}, ${pair.second.bottom}]")

                noteRects.add(pair)
            }
        }

        noteOnMap.values.sortedBy {it.first}.forEach {
            noteRects.add(it)
        }

        noteRects.forEachIndexed { i, pair ->
//            fgPaint.color = colors[i % colors.size]
//            Logger.debug("draw rect for tick [${pair.first}]: ${pair.second}")
            canvas.drawRect(pair.second, fgPaint)
        }
    }

    fun displayEvents(
        track: Track,
        tracksInfo: TrackInfo,
        channel: Int = 0) {
        Logger.debug("display event in track [${track}]: channel [$channel]")

        val displayEvents = mutableListOf<MidiEvent>()
        for (i in 0 until track.size()) {
            val e = track[i]
            val message = e.message
            if (message is ShortMessage && message.channel == channel) {
                displayEvents.add(e)
            }
        }

        for ((i, event) in displayEvents.withIndex()) {
            val message = event.message as ShortMessage
            Logger.debug("message [$i, tick: ${event.tick}]: ${message.command}, ${message.data1}, ${message.data2}")
        }

        channelEvents = displayEvents
        channelInfo = tracksInfo.channels[channel]

        invalidate()
    }

    fun viewTickFrom(tick: Long) {
        currentTick = tick
        Logger.debug("view tick from: $currentTick")
        invalidate()
    }


/*

    private var offscreenBitmap: Bitmap? = null
    private var offscreenCanvas: Canvas? = null
    private val paint: Paint? = null
    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)

        // Initialize the offscreen bitmap and canvas with the new size
        offscreenBitmap?.recycle() // Free up memory if the bitmap already exists

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        offscreenBitmap = bitmap
        offscreenCanvas = Canvas(bitmap)
    }
*/

}