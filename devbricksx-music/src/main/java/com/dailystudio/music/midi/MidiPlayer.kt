package com.dailystudio.music.midi

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer
import com.dailystudio.devbricksx.development.Logger
import jp.kshoji.javax.sound.midi.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException

class MidiPlayer(private val context: Context,
                 private val soundAsset: String) {

    private var synthesizer: SoftSynthesizer? = null
    private var sequencer: Sequencer? = null

    private val _playback: MutableStateFlow<MidiPlayback> = MutableStateFlow(MidiPlayback())
    val playback: StateFlow<MidiPlayback> = _playback

    private val _ready: MutableLiveData<Boolean> = MutableLiveData(false)
    val ready: LiveData<Boolean> = _ready

    var playing = false
    var paused = false

    var playingSequence: Sequence? = null
    var playingTempo: Float = MidiConstants.DEFAULT_SONG_TEMPO.toFloat()
    var playingSequenceTick: Long = 0L

    fun prepare() {
        try {
            val soundFont = SF2Soundbank(
                context.assets.open(soundAsset))

            synthesizer = SoftSynthesizer().apply {
                open()
                loadAllInstruments(soundFont)
                channels[0].programChange(MidiConstants.Program.AcousticGrandPiano.id)
            }

            sequencer = MidiSystem.getSequencer(synthesizer, eventReceiver)
            sequencer?.open()

            _ready.postValue(true)
        } catch (e: IOException) {
            Logger.error("failed to prepare: $e")
            _ready.postValue(false)
        } catch (e: MidiUnavailableException) {
            Logger.error("failed to prepare: $e")
            _ready.postValue(false)
        }

        Logger.info("player is ready")
    }

    fun release() {
        synthesizer?.close()
    }

    fun changeProgram(programId: Int) {
        changeProgram(0, programId)
    }

    fun changeProgram(channelId: Int = 0, programId: Int) {
        val synthesizerPid = programId - 1

        Logger.debug("change program: channel = $channelId, " +
                "programId = $programId [synth-pid: $synthesizerPid]")
        synthesizer?.channels?.get(channelId)?.programChange(synthesizerPid)

        Logger.debug("changed program: channel = $channelId, " +
                "programId = ${currentProgram(channelId) + 1} [synth-pid: ${currentProgram(channelId)}]")
    }


    fun currentProgram(channelId: Int = 0): Int {
        val synthesizerPid = synthesizer?.channels?.get(channelId)?.program
            ?: MidiConstants.DEFAULT_PROGRAM.id

        return synthesizerPid + 1
    }

    fun noteOn(note: Int , channelId: Int = 0) {
        Logger.debug("synthesizer: $synthesizer")
        Logger.debug("channels: ${synthesizer?.channels}")
        synthesizer?.channels?.get(channelId)?.noteOn(note, 200)
        Logger.debug("note on: $note")
    }

    fun noteOn(note: MidiNote, channelId: Int = 0) {
        noteOn(note.value())
    }

    fun noteOff(note: Int, channelId: Int = 0) {
        synthesizer?.channels?.get(channelId)?.noteOff(note, 100)
        Logger.debug("note off: $note")
    }

    fun noteOff(note: MidiNote, channelId: Int = 0) {
        noteOff(note.value())
    }

    suspend fun tapNote(note: Int,
                        length: Int = 1,
                        channelId: Int = 0,
                        ticksPerUnit: Int = 1,
                        msPerTick: Long = 50, ) {
        val delayTime = msPerTick * ticksPerUnit * length
        noteOn(note, channelId)
        Logger.debug("note delay: $delayTime")
        delay(delayTime)
        noteOff(note, channelId)
    }


    suspend fun tapNote(note: MidiNote,
                        channelId: Int = 0,
                        ticksPerUnit: Int = 1,
                        msPerTick: Long = 50, ) {
        tapNote(note.value(), note.length, channelId, ticksPerUnit, msPerTick)
    }

    private fun allNoteOff() {
        val channels = synthesizer?.channels ?: return
        for (c in channels) {
            c.allNotesOff()
            c.allSoundOff()
        }
    }

    fun play(sequence: Sequence,
             tempo: Float = MidiConstants.DEFAULT_SONG_TEMPO.toFloat()) {
        Logger.debug("play [tempo: $tempo]: $sequence")
        val sequencer = sequencer ?: return

        sequencer.tempoInBPM = tempo
        sequencer.sequence = sequence

        sequencer.loopStartPoint = 0L
        sequencer.start()

        playing = true
        paused = false
        playingSequence = sequence

        _playback.value = MidiPlayback(PlaybackEvent.Start, 0, 0)
    }

    fun reload(sequence: Sequence, tempo: Float) {
        val sequencer = sequencer ?: return

        sequencer.tempoInBPM = tempo
        sequencer.sequence = sequence

        playingSequence = sequence
    }

    fun stop() {
        val sequencer = sequencer ?: return

        allNoteOff()
        sequencer.stop()
        playing = false

        playingSequence = null
        playingSequenceTick = 0L

        _playback.value = MidiPlayback(PlaybackEvent.Stop, 0, 0)
    }

    fun pause() {
        val sequencer = sequencer ?: return

        playingSequenceTick = sequencer.tickPosition
        Logger.debug("paused at: $playingSequenceTick")
        allNoteOff()
        sequencer.stop()

        paused = true

        _playback.value = MidiPlayback(PlaybackEvent.Pause)
    }

    fun resume(sequence: Sequence, syncToTick: Long = -1L) {
        val sequencer = sequencer ?: return

        sequencer.sequence = sequence

        Logger.debug("resume at: $playingSequenceTick")

        sequencer.loopStartPoint = if (syncToTick == -1L) {
            playingSequenceTick
        } else {
            syncToTick
        }
        sequencer.start()

        paused = false

        _playback.value = MidiPlayback(PlaybackEvent.Resume)
    }

    private val eventReceiver = object: Receiver {

        override fun send(message: MidiMessage, timeStamp: Long) {
            Logger.debug("[playing: ${playing}] message coming[$timeStamp]: $message")
            if (playing) {
                if (message is ShortMessage) {
                    _playback.value =
                        MidiPlayback(
                        when (message.command) {
                            ShortMessage.NOTE_ON -> {
                                PlaybackEvent.NoteOn
                            }
                            ShortMessage.NOTE_OFF -> {
                                PlaybackEvent.NoteOff
                            }
                            else -> {
                                PlaybackEvent.None
                            }
                        },
                        data = message.data1,
                        timeStamp
                    )
                } else if (message is MetaMessage) {
                    val stop = (message.type == MetaMessage.TYPE_END_OF_TRACK)
                    _playback.value =
                        MidiPlayback(
                        when (message.type) {
                            MetaMessage.TYPE_END_OF_TRACK -> {
                                PlaybackEvent.Stop
                            }
                            else -> {
                                PlaybackEvent.None
                            }
                        },
                        0,
                        timeStamp
                    )

                    if (stop) {
                        playing = false
                        paused = false

                        playingSequence = null
                        playingSequenceTick = 0L
                    }
                }
            }
        }

        override fun close() {
        }

    }

}