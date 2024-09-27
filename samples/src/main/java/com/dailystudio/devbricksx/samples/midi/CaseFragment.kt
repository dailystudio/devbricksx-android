package com.dailystudio.devbricksx.samples.midi

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
import com.dailystudio.devbricksx.samples.midi.utils.MidiAnalyzer
import jp.kshoji.javax.sound.midi.ShortMessage
import jp.kshoji.javax.sound.midi.io.StandardMidiFileReader
import java.io.InputStream
import kotlin.math.min

class CaseFragment: DevBricksFragment() {

    private lateinit var midiPlayer: MidiPlayer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupPlayer()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_case_midi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun loadMidi() {
        val assets = requireContext().assets

//        val inputStream: InputStream = assets.open("midi/love.mid") // Load your MIDI file from assets
        val inputStream: InputStream = assets.open("midi/PokerFace.mid") // Load your MIDI file from assets

        val fileReader = StandardMidiFileReader()
        val sequence =  StandardMidiFileReader().getSequence(inputStream)

        val analyzer = MidiAnalyzer(sequence)

        analyzer.analyze()
        val tracksInfo = analyzer.getTracksInfo()
        tracksInfo.entries.forEach { entry ->
            Logger.debug("track info[${entry.key}]: ")

            entry.value.programs.entries.forEach { entry ->
                Logger.debug("channel [${entry.key}] program: ${entry.value}")
            }

            entry.value.ranges.entries.forEach { entry ->
                val min = entry.value.first
                val max = entry.value.second
                val range = (max - min)
                Logger.debug("channel [${entry.key}] note range: [$min, $max], $range")
            }
        }

        midiPlayer.play(sequence, 160F)
    }

    private fun setupPlayer() {
        midiPlayer = MidiPlayer(
            requireContext(),
            "soundfont/FluidR3_GM.sf2"
        )

        midiPlayer.playback.observe(this) {
//            Logger.debug("Player: event: ${it.event.name}")
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