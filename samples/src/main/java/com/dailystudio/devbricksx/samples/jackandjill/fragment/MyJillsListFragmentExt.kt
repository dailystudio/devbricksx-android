package com.dailystudio.devbricksx.samples.jackandjill.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.jackandjill.Midi
import com.dailystudio.devbricksx.samples.jackandjill.MyJill
import com.dailystudio.devbricksx.samples.jackandjill.model.MyJillViewModelExt
import com.dailystudio.music.midi.MidiPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MyJillsListFragmentExt: MyJillsListFragment() {

    private lateinit var myJillViewModelExt: MyJillViewModelExt
    private lateinit var player: MidiPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myJillViewModelExt = ViewModelProvider(this)[MyJillViewModelExt::class.java]

        player = MidiPlayer(
            requireContext(),
            "GeneralUser GS MuseScore v1.sf2"
        )

        player.playback.observe(this) {
            Logger.debug("Player: event: ${it.event.name}")
        }

        player.ready.observe(this) {
            Logger.debug("Player: ready: $it")
            if (it) {
                lifecycleScope.launch(Dispatchers.IO) {
//                    player.changeProgram(programId = MidiConstants.DEFAULT_PROGRAM.id)
//                    player.play(Midi.sequence, 90f)
                }
            }
        }

        myJillViewModelExt.jillQuestion.observe(this) {
            Logger.debug("observed cmd: ${it.topic}")
            when(it.topic) {
                "play" -> player.play(
                    Midi.sequences[Random.nextInt(0, Midi.sequences.size)],
                    90f)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                player.prepare()
            }
        }
    }

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)

        val idView: TextView? = fragmentView.findViewById(R.id.my_name)
        idView?.text = buildString {
            append(MyJillViewModelExt.myJillName)
            append('(')
            append(MyJillViewModelExt.myJillId)
            append(')')
        }
    }

    override fun onItemClick(
        recyclerView: RecyclerView,
        itemView: View,
        position: Int,
        item: MyJill,
        id: Long
    ) {
        super.onItemClick(recyclerView, itemView, position, item, id)

        myJillViewModelExt.confirmReady(item.id)
    }

}