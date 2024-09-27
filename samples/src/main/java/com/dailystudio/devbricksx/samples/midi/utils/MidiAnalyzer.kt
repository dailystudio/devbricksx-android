package com.dailystudio.devbricksx.samples.midi.utils

import com.dailystudio.devbricksx.development.Logger
import jp.kshoji.javax.sound.midi.Sequence
import jp.kshoji.javax.sound.midi.ShortMessage
import kotlin.math.max
import kotlin.math.min

data class TrackInfo(
    val programs: Map<Int, Int> = emptyMap(),
    val ranges: Map<Int, Pair<Int, Int>> = emptyMap(),
)

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

        val mapOfPrograms = mutableMapOf<Int, Int>()
        val mapOfRanges = mutableMapOf<Int, Pair<Int, Int>>()

        val track = sequence.tracks[trackIndex]
        for (i in 0 until track.size()) {
            val event = track[i].message
            if (event is ShortMessage) {
                if (event.command == ShortMessage.PROGRAM_CHANGE) {
                    Logger.debug("channel [${event.channel}] program change: ${event.data1}")
                    mapOfPrograms[event.channel] = event.data1
                } else if (event.command == ShortMessage.NOTE_ON) {
                    var range = mapOfRanges[event.channel]
                    if (range == null) {
                        range = Pair(128, -1)
                    }

                    range = Pair(min(range.first, event.data1), max(range.second, event.data1))
                    mapOfRanges[event.channel] = range
                }
            }
        }

        return TrackInfo(
            mapOfPrograms,
            mapOfRanges
        )
    }

    fun getTracksInfo(): Map<Int, TrackInfo> {
        return dataOfTracks
    }

}