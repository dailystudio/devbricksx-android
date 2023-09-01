package com.dailystudio.music.midi

class MidiNote(val octave: MidiConstants.Octave = MidiConstants.Octave.C5,
               val pitch: MidiConstants.PitchName = MidiConstants.PitchName.C) {

    var length: Int = 1

    fun value(): Int {
        return octave.leadPitch + pitch.offset
    }

    override fun toString(): String {
        return buildString {
            append(octave)
            append(" - ")
            append(pitch)
            append(" (val: ")
            append("${value()}")
            append(", ")
            append("len: ")
            append(this@MidiNote.length)
            append(')')
        }
    }
}