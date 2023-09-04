package com.dailystudio.devbricksx.samples.jackandjill.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.core.R as coreR
import com.dailystudio.devbricksx.samples.jackandjill.Midi
import com.dailystudio.devbricksx.samples.jackandjill.NearByJill
import com.dailystudio.devbricksx.samples.jackandjill.model.NearByJillViewModelExt
import com.dailystudio.music.midi.MidiPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class NearByJillsListFragmentExt: NearByJillsListFragment() {

    private lateinit var myJillViewModelExt: NearByJillViewModelExt
    private lateinit var player: MidiPlayer

    private var readyBtn: Button? = null
    private var playBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        myJillViewModelExt = ViewModelProvider(this)[NearByJillViewModelExt::class.java]


        setupPlayer()

        myJillViewModelExt.ready.observe(this) {
            readyBtn?.text = getString(
                if (it) coreR.string.btn_not_ready else coreR.string.btn_ready
            )
        }

        myJillViewModelExt.jillQuestion.observe(this) {
            Logger.debug("observed cmd: ${it.topic}")
            when(it.topic) {
                NearByJillViewModelExt.TOPIC_PLAY -> {
                    val seqIndex = try {
                        it.extras[NearByJillViewModelExt.KEY_SEQ]?.toInt()
                    } catch (e: Exception) {
                        Logger.error("failed to parse seqIndex from [$it]: $e")
                        -1
                    } ?: -1

                    if (seqIndex >= 0 && seqIndex < Midi.sequences.size) {
                        player.play(
                            Midi.sequences[seqIndex],
                            90f)
                    }
                }
            }
        }


    }

    private fun setupPlayer() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                player.prepare()
            }
        }
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
    }

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)

        val idView: TextView? = fragmentView.findViewById(R.id.my_name)
        idView?.text = buildString {
            append(NearByJillViewModelExt.myJillName)
            append('(')
            append(NearByJillViewModelExt.myJillId)
            append(')')
        }

        readyBtn = fragmentView.findViewById(R.id.btn_ready)
        readyBtn?.setOnClickListener {
            myJillViewModelExt.toggleReady()
        }

        playBtn = fragmentView.findViewById(R.id.btn_play)
        playBtn?.setOnClickListener {
            player.play(Midi.sequences[0], 90f)
            myJillViewModelExt.startPlay()
        }
    }

    override fun onItemClick(
        recyclerView: RecyclerView,
        itemView: View,
        position: Int,
        item: NearByJill,
        id: Long
    ) {
        super.onItemClick(recyclerView, itemView, position, item, id)
    }

}