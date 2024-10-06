package com.dailystudio.music.midi

import com.dailystudio.devbricksx.development.Logger
import jp.kshoji.javax.sound.midi.MetaMessage
import jp.kshoji.javax.sound.midi.Sequence
import jp.kshoji.javax.sound.midi.ShortMessage
import kotlin.math.max
import kotlin.math.min

data class NotesInfo(
    var min: Int = 128,
    var max: Int = -1
) {
    val range: Int = (max - min) + 1
    val valid: Boolean = (range >= 0)

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
) {
    override fun toString(): String {
        return buildString {
            append("program: $program, ")
            append("notes: $notes")
        }
    }
}

data class TrackInfo(
    val channels: Map<Int, ChannelInfo> = emptyMap(),
    val tempo: Float = 120f
) {
    override fun toString(): String {
        return buildString {
            append("\n[")
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

        val track = sequence.tracks[trackIndex]
        for (i in 0 until track.size()) {
            val event = track[i].message
            if (event is ShortMessage) {
                if (event.command == ShortMessage.PROGRAM_CHANGE) {
                    Logger.debug("channel [${event.channel}] program change: ${event.data1}")

                    mapOfChannels[event.channel] = mapOfChannels.getOrPut(event.channel) {
                        ChannelInfo()
                    }.copy(program = event.data1)
                } else if (event.command == ShortMessage.NOTE_ON) {
                    val channelInfo = mapOfChannels.getOrPut(event.channel) {
                        ChannelInfo()
                    }

                    mapOfChannels[event.channel] = channelInfo.copy(
                        notes = NotesInfo(
                            min(channelInfo.notes.min, event.data1),
                            max(channelInfo.notes.max, event.data1)
                        )
                    )
                }
            } else if (event is MetaMessage && event.type == MetaMessage.TYPE_TEMPO) {
                // Extract the microseconds per quarter note (stored in event data)
                val tempoBytes = event.data // 3-byte array
                val microsecondsPerQuarterNote = (tempoBytes[0].toInt() shl 16) or
                        (tempoBytes[1].toInt() shl 8) or
                        tempoBytes[2].toInt()

                // Convert to BPM
                tempo = 60000000.0F / microsecondsPerQuarterNote
                if (tempo < 0) {
                    tempo = 120F
                }
            }
        }

        return TrackInfo(
            mapOfChannels,
            tempo
        )
    }

    fun getTracksInfo(): Map<Int, TrackInfo> {
        return dataOfTracks
    }

}