package com.dailystudio.devbricksx.samples.jackandjill.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.jackandjill.model.MyJillViewModelExt
import com.dailystudio.music.midi.MidiConstants
import com.dailystudio.music.midi.MidiNote
import com.dailystudio.music.midi.MidiPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyJillsListFragmentExt: MyJillsListFragment() {

    private lateinit var viewModel: MyJillViewModelExt
    private lateinit var player: MidiPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MyJillViewModelExt::class.java]

        player = MidiPlayer(
            requireContext(),
            "GeneralUser GS MuseScore v1.sf2"
        ).apply {
        }

        player.playback.observe(this) {
            Logger.debug("Player: event: ${it.event.name}")
        }

        player.ready.observe(this) {
            Logger.debug("Player: ready: $it")
            if (it) {
                lifecycleScope.launch(Dispatchers.IO) {
                    player.changeProgram(programId = MidiConstants.DEFAULT_PROGRAM.id)
                    player.tapNote(
                        MidiNote(MidiConstants.Octave.C5).apply {
                            length = 20
                        }
                    )
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                player.prepare()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)

        val idView: TextView? = fragmentView.findViewById(R.id.my_name)
        idView?.text = buildString {
            append(viewModel.jillName)
            append('(')
            append(viewModel.jillId)
            append(')')
        }
    }

}