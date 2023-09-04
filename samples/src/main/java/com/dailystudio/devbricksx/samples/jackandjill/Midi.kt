package com.dailystudio.devbricksx.samples.jackandjill

import com.dailystudio.music.midi.MidiConstants
import com.dailystudio.music.midi.MidiNote
import jp.kshoji.javax.sound.midi.MidiEvent
import jp.kshoji.javax.sound.midi.Sequence
import jp.kshoji.javax.sound.midi.ShortMessage
import jp.kshoji.javax.sound.midi.Track

object Midi {

    private val notes1 = arrayOf(
        MidiNote(MidiConstants.Octave.C5, MidiConstants.PitchName.C),
        MidiNote(MidiConstants.Octave.C5, MidiConstants.PitchName.D),
        MidiNote(MidiConstants.Octave.C5, MidiConstants.PitchName.E),
    )

    private val notes2 = arrayOf(
        MidiNote(MidiConstants.Octave.C1, MidiConstants.PitchName.G),
        MidiNote(MidiConstants.Octave.C3, MidiConstants.PitchName.G),
        MidiNote(MidiConstants.Octave.C5, MidiConstants.PitchName.G),
    )

    private val notes3 = arrayOf(
        MidiNote(MidiConstants.Octave.C6, MidiConstants.PitchName.D),
        MidiNote(MidiConstants.Octave.C6, MidiConstants.PitchName.D),
        MidiNote(MidiConstants.Octave.C6, MidiConstants.PitchName.D),
    )

    val sequence1 = Sequence(Sequence.PPQ, 24)
        .apply {
            val track: Track = createTrack()

            val programChangeMessage = ShortMessage().apply {
                setMessage(ShortMessage.PROGRAM_CHANGE, 0, MidiConstants.DEFAULT_PROGRAM.id - 1, 0)
            }

            track.add(MidiEvent(programChangeMessage, 0))

            for ((i, note) in notes1.withIndex()) {
                val noteOnMsg = ShortMessage()
                noteOnMsg.setMessage(
                    ShortMessage.NOTE_ON,
                    0, note.value(), 100)
                val noteOffMsg = ShortMessage()
                noteOffMsg.setMessage(
                    ShortMessage.NOTE_OFF,
                    0, note.value(), 100)

                val ticks = (24 / 4).toLong()

                track.add(MidiEvent(noteOnMsg, i * ticks))
                track.add(MidiEvent(noteOffMsg, (i + 4) * ticks))
            }
        }

    val sequence2 = Sequence(Sequence.PPQ, 24)
        .apply {
            val track: Track = createTrack()

            val programChangeMessage = ShortMessage().apply {
                setMessage(ShortMessage.PROGRAM_CHANGE, 0, MidiConstants.DEFAULT_PROGRAM.id - 1, 0)
            }

            track.add(MidiEvent(programChangeMessage, 0))

            for ((i, note) in notes2.withIndex()) {
                val noteOnMsg = ShortMessage()
                noteOnMsg.setMessage(
                    ShortMessage.NOTE_ON,
                    0, note.value(), 100)
                val noteOffMsg = ShortMessage()
                noteOffMsg.setMessage(
                    ShortMessage.NOTE_OFF,
                    0, note.value(), 100)

                val ticks = (24 / 4).toLong()

                track.add(MidiEvent(noteOnMsg, i * ticks))
                track.add(MidiEvent(noteOffMsg, (i + 4) * ticks))
            }
        }

    val sequence3 = Sequence(Sequence.PPQ, 24)
        .apply {
            val track: Track = createTrack()

            val programChangeMessage = ShortMessage().apply {
                setMessage(ShortMessage.PROGRAM_CHANGE, 0, MidiConstants.DEFAULT_PROGRAM.id - 1, 0)
            }

            track.add(MidiEvent(programChangeMessage, 0))

            for ((i, note) in notes3.withIndex()) {
                val noteOnMsg = ShortMessage()
                noteOnMsg.setMessage(
                    ShortMessage.NOTE_ON,
                    0, note.value(), 100)
                val noteOffMsg = ShortMessage()
                noteOffMsg.setMessage(
                    ShortMessage.NOTE_OFF,
                    0, note.value(), 100)

                val ticks = (24 / 4).toLong()

                track.add(MidiEvent(noteOnMsg, i * ticks))
                track.add(MidiEvent(noteOffMsg, (i + 4) * ticks))
            }
        }


    var sequences = arrayOf(
        sequence1,
        sequence2,
        sequence3,
    )

}