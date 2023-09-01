package com.dailystudio.music.midi

enum class PlaybackEvent {
    Start,
    NoteOn,
    NoteOff,
    Stop,
    Pause,
    Resume,
    None,
}

class MidiPlayback(val event: PlaybackEvent = PlaybackEvent.None,
                   val data: Int = 0,
                   val tick: Long = 0) {

    override fun toString(): String {
        return buildString {
            append("[$tick]: ")
            append("$event ")
            append("(data: $data)")
        }
    }
}