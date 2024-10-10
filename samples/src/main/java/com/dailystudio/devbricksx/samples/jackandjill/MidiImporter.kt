package com.dailystudio.devbricksx.samples.jackandjill

import android.content.Context
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.FileUtils
import com.dailystudio.devbricksx.music.midi.MidiConstants
import com.dailystudio.devbricksx.music.midi.MidiNote
import com.google.gson.Gson
import jp.kshoji.javax.sound.midi.MidiEvent
import jp.kshoji.javax.sound.midi.Sequence
import jp.kshoji.javax.sound.midi.ShortMessage
import jp.kshoji.javax.sound.midi.Track
import okio.ByteString.Companion.decodeBase64
import java.nio.ByteOrder


class NoteTap(
    var row: Int = -1,
    var col: Int = -1
) {

    var note: MidiNote = MidiNote()

    companion object {

        fun fromString(str: String): NoteTap {
            val tap = NoteTap()

            val parts = str.split("_")
            if (parts.size != 2) {
                return tap
            }

            try {
                tap.row = parts[0].toInt()
                tap.col = parts[1].toInt()
            } catch (e: NumberFormatException) {
                Logger.error("failed to part note tap from [$str]: $e")
            }

            return tap
        }
    }

    fun getPosKey(): String {
        return buildString {
            append("$row")
            append('_')
            append("$col")
        }
    }

    override fun toString(): String {

        return buildString {
            append("${this@NoteTap.javaClass.simpleName}(${this@NoteTap.hashCode()})")
            append("[$row")
            append(", ")
            append("$col]:")
            append(note)
        }

    }

}

class PadSettings(
    val bars: Int = 16,
    val beatsPerBar: Int = 4,
    val splitsPerBeat: Int = 2,
    val tempoInBPM: Float = 120f,
    val PPQ: Int = 24,
) {
    override fun toString(): String {
        return buildString {
            append("bars: $bars")
            append(", beats/bar: $beatsPerBar")
            append(", splits/beat: $beatsPerBar")
            append(", tempo: $tempoInBPM (per minute)")
            append(", PPQ: $PPQ")
        }
    }
}

class Song(
    val title: String,
    val settings: PadSettings,
    val tracks: Array<String>
) {
    override fun toString(): String {
        return buildString {
            append("title: [$title]")
            append("settings: [$settings]")
            append("tracks: $tracks")
        }
    }

    fun toMidiSequences(): Array<Sequence> {
        val listOfSequences = mutableListOf<Sequence>()

        for (t in tracks) {
            val track = MidiImporter.decodeBase64TrackData(t) ?: continue

            val sequence = Sequence(Sequence.PPQ, settings.PPQ)
                .apply {
                    MidiImporter.attachToSequence(
                        this, 0, track.first - 1, track.second, settings
                    )
                }
            listOfSequences.add(sequence)
        }

        return listOfSequences.toTypedArray()
    }

}

object MidiImporter {

    private val GSON = Gson()

    fun loadSong(context: Context, asset: String): Song? {
        val str = try {
            FileUtils.assetToString(context, asset)
        } catch (e: Exception) {
            Logger.error("failed to load song from [$asset]: $e")
            null
        }

        Logger.debug("song str: [$str]")

        return str?.let {
            try {
                GSON.fromJson(it, Song::class.java)

            } catch (e: Exception) {
                Logger.error("failed to convert song from [$it]: $e")
                null
            }
        }
    }

    private fun bytesToUnsignedShort(byte1 : Byte, byte2 : Byte, bigEndian : Boolean) : Int {
        Logger.debug("byte1: $byte1")
        Logger.debug("byte2: $byte2")
        if (bigEndian)
            return (((byte1.toUByte().toInt() and 255) shl 8) or (byte2.toUByte().toInt() and 255))

        return (((byte2.toUByte().toInt() and 255) shl 8) or (byte1.toUByte().toInt() and 255))
    }

    fun decodeBase64TrackData(base64string: String): Pair<Int, List<NoteTap>>? {
        Logger.debug("[IMPORT] buffer: base64string = $base64string")
        val bytes = base64string.decodeBase64() ?: return null
        val buffer = bytes.asByteBuffer()
        val endian = buffer.order()
        Logger.debug("[IMPORT] buffer: endian = ${buffer.order()}")
        Logger.debug("[IMPORT] buffer: size = ${buffer.remaining()}")
        val programInt = buffer.get().toUByte().toInt()
        Logger.debug("[IMPORT] buffer: programInt = $programInt")
        val program = MidiConstants.Program.valueOf(programInt) ?: MidiConstants.DEFAULT_PROGRAM
        Logger.debug("[IMPORT] buffer: program = $program")

        val taps = mutableListOf<NoteTap>()
        val countOfTaps = bytesToUnsignedShort(buffer.get(),
            buffer.get(), endian == ByteOrder.BIG_ENDIAN)
        Logger.debug("[IMPORT] buffer: countOfTaps = $countOfTaps")

        for (i in 0 until  countOfTaps) {
            val row = buffer.get().toUByte().toInt()
            val col = buffer.get().toUByte().toInt()

            val octave = MidiConstants.Octave.valueOf(
                buffer.get().toUByte().toInt()) ?: MidiConstants.Octave.C5
            val pitch = MidiConstants.PitchName.valueOf(
                buffer.get().toUByte().toInt()) ?: MidiConstants.PitchName.C
            val noteLength = buffer.get().toUByte().toInt()

            val midiNote = MidiNote(octave, pitch).apply {
                length = noteLength
            }

            val noteTap = NoteTap(row, col).apply {
                note = midiNote
            }

            taps.add(noteTap)
        }

        return Pair(program.id, taps)
    }


    fun exportSequence(tracks: Map<Int, Pair<Int, List<NoteTap>>>,
                       padSettings: PadSettings): Sequence {
        val sequence = Sequence(Sequence.PPQ, padSettings.PPQ)

        for ((channelId, track) in tracks) {
            val program = track.first - 1 /* synthesizer program id */
            val taps = track.second

            attachToSequence(sequence, channelId,
                program, taps, padSettings)
        }

        return sequence
    }

    fun attachToSequence(sequence: Sequence,
                         channelId: Int,
                         programId: Int,
                         taps: List<NoteTap>,
                         padSettings: PadSettings) {
        Logger.debug("attach to channel[$channelId]: program = $programId, taps = $taps")
        val track: Track = sequence.createTrack()

        val programChangeMessage = ShortMessage().apply {
            setMessage(ShortMessage.PROGRAM_CHANGE, channelId, programId, 0)
        }

        track.add(MidiEvent(programChangeMessage, 0))

        for (tap in taps) {
            val noteOnMsg = ShortMessage()
            noteOnMsg.setMessage(
                ShortMessage.NOTE_ON,
                channelId, tap.note.value(), 100)
            val noteOffMsg = ShortMessage()
            noteOffMsg.setMessage(
                ShortMessage.NOTE_OFF,
                channelId, tap.note.value(), 100)

            val ticks = (padSettings.PPQ / padSettings.splitsPerBeat).toLong()

            track.add(MidiEvent(noteOnMsg, tap.col * ticks))
            track.add(MidiEvent(noteOffMsg, (tap.col + tap.note.length) * ticks))
        }
    }

}