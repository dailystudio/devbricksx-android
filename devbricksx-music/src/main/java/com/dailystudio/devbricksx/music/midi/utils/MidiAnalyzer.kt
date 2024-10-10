package com.dailystudio.devbricksx.music.midi.utils

import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.CalendarUtils
import jp.kshoji.javax.sound.midi.MetaMessage
import jp.kshoji.javax.sound.midi.Sequence
import jp.kshoji.javax.sound.midi.ShortMessage
import java.nio.charset.StandardCharsets
import kotlin.math.max
import kotlin.math.min

data class NotesInfo(
    var min: Int = 128,
    var max: Int = -1,
) {
    val range: Int
        get() = (max - min) + 1

    override fun toString(): String {
        return buildString {
            append("[min: $min, ")
            append("max: $max], ")
            append("range: $range")
        }
    }
}

data class ChannelInfo(
    val program: Int = 0,
    val notes: NotesInfo = NotesInfo(),
    var startTime: Long = Long.MAX_VALUE,
    var endTime: Long = Long.MIN_VALUE
) {
    val length: Long
        get() = endTime - startTime
    val valid: Boolean
        get() = (notes.range >= 1 && length > 0)

    override fun toString(): String {
        return buildString {
            append("time: ${startTime}ms ~ ${endTime}ms (length: ${CalendarUtils.durationToReadableString(this@ChannelInfo.length)}ms), ")
            append("program: $program, ")
            append("notes: $notes, ")
            append("valid: $valid")
        }
    }
}

data class TrackInfo(
    val channels: Map<Int, ChannelInfo> = emptyMap(),
    val tempo: Float = 120f,
    val name: String = "",
) {
    override fun toString(): String {
        return buildString {
            append("\n[name: $name")
            append("\n  tempo: $tempo, ")
            append("\n  channels: [")
            channels.forEach { entry ->
                append("\n    [${entry.key}]: [${entry.value}]")
            }
            append("\n  ]")
            append("\n]")
        }
    }
}

class MidiAnalyzer(
    private val sequence: Sequence
) {
    private val dataOfTracks = mutableMapOf<Int, TrackInfo>()

    fun analyze() {
        val start = System.currentTimeMillis()
        for (i in 0 until sequence.tracks.size) {
            dataOfTracks[i] = analyzeTrack(i)
        }
        val end = System.currentTimeMillis()
        Logger.debug("analyzed in ${end - start} ms")
    }

    private fun analyzeTrack(trackIndex: Int = 0): TrackInfo {
        if (trackIndex < 0 || trackIndex >= sequence.tracks.size) {
            return TrackInfo()
        }

        val mapOfChannels = mutableMapOf<Int, ChannelInfo>()
        var tempo = 120f
        var name = ""

        val track = sequence.tracks[trackIndex]
        for (i in 0 until track.size()) {
            val event = track[i]
            val tick = event.tick
            val message = event.message

            if (message is ShortMessage) {
                if (message.command == ShortMessage.PROGRAM_CHANGE) {
                    Logger.debug("channel [${message.channel}] program change: ${message.data1}")

                    mapOfChannels[message.channel] = mapOfChannels.getOrPut(message.channel) {
                        ChannelInfo()
                    }.copy(program = message.data1)
                } else if (message.command == ShortMessage.NOTE_ON ) {
                    val channelInfo = mapOfChannels.getOrPut(message.channel) {
                        ChannelInfo()
                    }

                    val data1 = message.data1
                    val data2 = message.data2

                    val newChannelInfo = channelInfo.copy(
                        notes = NotesInfo(
                            min(channelInfo.notes.min, data1),
                            max(channelInfo.notes.max, data1),
                        ),
                    )

                    newChannelInfo.startTime = min(channelInfo.startTime, tick)

                    if (data2 > 0) {
                        newChannelInfo.endTime = max(newChannelInfo.endTime, tick)
                    }

                    mapOfChannels[message.channel] = newChannelInfo
                }
            } else if (message is MetaMessage) {
                if (message.data.size > 1) {
                    Logger.debug("meta event[${tick}]: type = ${message.type.toString(16)}")
                }
                if (message.type == MetaMessage.TYPE_TEMPO) {
                    // Extract the microseconds per quarter note (stored in event data)
                    val tempoBytes = message.data // 3-byte array
                    val microsecondsPerQuarterNote = (tempoBytes[0].toInt() shl 16) or
                            (tempoBytes[1].toInt() shl 8) or
                            tempoBytes[2].toInt()

                    // Convert to BPM
                    tempo = 60000000.0F / microsecondsPerQuarterNote
                    if (tempo < 0) {
                        tempo = 120F
                    }
                } else if (message.type == 0x03) {
                    name =  String(message.data, StandardCharsets.UTF_8)
                } else if (message.type == 0x01) {
                    if (tick == 0L) {
                        name = String(message.data, StandardCharsets.UTF_8)
                    }
                }
            }
        }

        return TrackInfo(
            mapOfChannels,
            tempo,
            name
        )
    }

    fun getTracksInfo(): Map<Int, TrackInfo> {
        return dataOfTracks
    }

}