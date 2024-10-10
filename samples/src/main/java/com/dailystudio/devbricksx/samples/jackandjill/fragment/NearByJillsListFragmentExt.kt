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
import com.dailystudio.devbricksx.samples.jackandjill.Midi
import com.dailystudio.devbricksx.samples.jackandjill.NearByJill
import com.dailystudio.devbricksx.samples.jackandjill.model.NearByJillViewModelExt
import com.dailystudio.devbricksx.utils.CalendarUtils
import com.dailystudio.devbricksx.music.midi.MidiPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.dailystudio.devbricksx.samples.core.R as coreR


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

                    val timeLocal = System.currentTimeMillis()
                    val timeSync = try {
                        it.extras[NearByJillViewModelExt.KEY_TIME_SYNC]?.toLong()
                    } catch (e: Exception) {
                        Logger.error("failed to parse time sync from [$it]: $e")
                        -1L
                    } ?: -1L

                    val timeShift = if (timeSync != -1L) {
                        timeLocal - timeSync
                    } else {
                        0L
                    }


                    val timeStart = try {
                        it.extras[NearByJillViewModelExt.KEY_TIME_START]?.toLong()
                    } catch (e: Exception) {
                        Logger.error("failed to parse time from [$it]: $e")
                        -1L
                    } ?: -1L

                    val timeTarget = timeStart + timeShift

                    Logger.debug("time local: $timeStart [${CalendarUtils.timeToReadableString(timeLocal)}]")
                    Logger.debug("time sync: $timeSync [${CalendarUtils.timeToReadableString(timeSync)}]")
                    Logger.debug("time shift: $timeShift")
                    Logger.debug("time target: $timeTarget [${CalendarUtils.timeToReadableString(timeTarget)}]")

                    if (seqIndex >= 0 && seqIndex < Midi.sequences.size) {
                        val now = System.currentTimeMillis()
                        lifecycleScope.launch(Dispatchers.IO) {
                            if (timeTarget > now) {
                                delay(timeTarget - now)
                            }
                            player.play(
                                Midi.sequences[seqIndex])

                        }
                    }
                }
            }
        }


    }

    private fun setupPlayer() {
        player = MidiPlayer(
            requireContext(),
            "soundfont/FluidR3_GM.sf2"
        )


        player.ready.observe(this) {
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
                player.prepare()
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
            val currentTimeMillis = System.currentTimeMillis()

            val remainder = currentTimeMillis % 10000

            val timeUntilNext10s = 10000 - remainder

            val nearestFutureTimeMillis = currentTimeMillis + timeUntilNext10s

            Logger.debug("curr time: $currentTimeMillis [${CalendarUtils.timeToReadableString(currentTimeMillis)}")
            Logger.debug("start time: $currentTimeMillis [${CalendarUtils.timeToReadableString(nearestFutureTimeMillis)}")

            lifecycleScope.launch(Dispatchers.IO) {
                delay(nearestFutureTimeMillis - currentTimeMillis)
                player.play(Midi.sequences[0])
            }

            myJillViewModelExt.startPlay(nearestFutureTimeMillis)
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