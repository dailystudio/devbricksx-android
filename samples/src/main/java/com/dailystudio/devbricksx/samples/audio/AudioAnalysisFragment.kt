package com.dailystudio.devbricksx.samples.audio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dailystudio.devbricksx.audio.AudioConfig
import com.dailystudio.devbricksx.audio.AudioProcessFragment
import com.dailystudio.devbricksx.samples.R

class AudioAnalysisFragment : AudioProcessFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_audio_analysis, container, false)

    override fun onProcessAudioData(audioConfig: AudioConfig, audioData: ShortArray) {
    }

}