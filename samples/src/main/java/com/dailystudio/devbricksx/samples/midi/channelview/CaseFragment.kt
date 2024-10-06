package com.dailystudio.devbricksx.samples.midi.channelview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.fragment.DevBricksFragment
import com.dailystudio.music.midi.MidiPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.repeatOnLifecycle
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.music.midi.MidiAnalyzer
import com.dailystudio.music.midi.PlaybackEvent
import com.dailystudio.music.midi.ui.MidiChannelViewer
import jp.kshoji.javax.sound.midi.io.StandardMidiFileReader
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.InputStream

class CaseFragment: DevBricksFragment() {

    private lateinit var midiPlayer: MidiPlayer

    private var midiChannelViewer: MidiChannelViewer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPlayer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_case_midi_channel_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        midiChannelViewer = view.findViewById(R.id.channel_view)
    }

    suspend fun loadMidi() {
        val assets = requireContext().assets

//        val inputStream: InputStream = assets.open("midi/love.mid") // Load your MIDI file from assets
        val inputStream: InputStream = assets.open("midi/PokerFace.mid") // Load your MIDI file from assets
//        val inputStream: InputStream = assets.open("midi/twinkle_twinkle_little_star.mid") // Load your MIDI file from assets

        val fileReader = StandardMidiFileReader()
        val sequence =  fileReader.getSequence(inputStream)

        val analyzer = MidiAnalyzer(sequence)

        analyzer.analyze()
        val tracksInfo = analyzer.getTracksInfo()
        tracksInfo.entries.forEach { entryOfTrack ->
            Logger.debug("track info[${entryOfTrack.key}]: ${entryOfTrack.value}")
        }

        withContext(Dispatchers.Main) {
            if (tracksInfo.isNotEmpty()) {
                midiChannelViewer?.setBars(3)
                midiChannelViewer?.displayEvents(
                    sequence.tracks[0],
                    tracksInfo[0]!!,
                    channel = 1
                )
            }
        }

        midiPlayer.play(sequence)

        lifecycleScope.launch {
            val run = true

            if (run) {
                val now = System.currentTimeMillis()
                var tick = now

                while ((tick - now) <= 5000L) {
                    tick = System.currentTimeMillis() - now
                    withContext(Dispatchers.Main) {
                        midiChannelViewer?.viewTickFrom(tick)
                    }

                    delay(5L)
                }
            } else {
                withContext(Dispatchers.Main) {
                    midiChannelViewer?.viewTickFrom(0)
                }
            }
        }

    }

    private fun setupPlayer() {
        midiPlayer = MidiPlayer(
            requireContext(),
            "soundfont/FluidR3_GM.sf2"
        )

        var playJob: Job? = null

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                midiPlayer.playback.collect {
                    Logger.debug("Player: event: ${it.event.name}")

                    when (it.event) {
                        PlaybackEvent.Start -> {
                            playJob = lifecycleScope.launch {
                                val now = System.currentTimeMillis()
                                var tick = now

                                while (true) {
                                    tick = System.currentTimeMillis() - now
                                    withContext(Dispatchers.Main) {
                                        midiChannelViewer?.viewTickFrom(tick)
                                    }

                                    delay(5L)
                                }
                            }
                        }
                        PlaybackEvent.Stop -> {
                            playJob?.cancel()
                        }
                        else -> {}
                    }
                }
            }
        }
        midiPlayer.ready.observe(this) {
            Logger.debug("Player: ready: $it")
            if (it) {
                lifecycleScope.launch(Dispatchers.IO) {
//                    player.changeProgram(programId = MidiConstants.DEFAULT_PROGRAM.id)
//                    player.play(Midi.sequence, 90f)
                }
            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                midiPlayer.prepare()

                loadMidi()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        midiPlayer.stop()
    }
}