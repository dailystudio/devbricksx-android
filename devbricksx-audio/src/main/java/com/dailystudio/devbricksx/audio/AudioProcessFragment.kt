package com.dailystudio.devbricksx.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.async.ManagedThread
import com.dailystudio.devbricksx.audio.visualizer.RawAudioDataVisualizer
import com.dailystudio.devbricksx.development.Logger
import kotlinx.android.synthetic.main.fragment_audio_process.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.locks.ReentrantLock
import kotlin.math.max

data class AudioConfig(val audioSource: Int,
                       val sampleRate: Int,
                       val sampleDurationInMs: Int,
                       val channelConfig: Int,
                       val audioFormat: Int) {

    fun getRecordingBufferLength(): Int {
        return (sampleRate * sampleDurationInMs / 1000)
    }

}

abstract class AudioProcessFragment : AbsAudioFragment() {

    companion object {
        private const val DEFAULT_SAMPLE_RATE = 16000
        private const val DEFAULT_SAMPLE_DURATION_MS = 1000
        private const val MINIMUM_TIME_BETWEEN_SAMPLES_MS: Long = 30
    }

    private lateinit var recordingBuffer: ShortArray
    private var recordingOffset = 0
    private val recordingBufferLock = ReentrantLock()

    private var processingStarted = false

    private var visualizer: RawAudioDataVisualizer? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_audio_process, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visualizer = view.findViewById(R.id.visualizer)
    }

    override fun onPermissionsGranted(newlyGranted: Boolean) {
        startProcessing()
    }

    override fun onResume() {
        super.onResume()

        if (isPermissionsGranted()) {
            startProcessing()
        }
    }

    override fun onPause() {
        super.onPause()

        stopProcessing()
    }

    protected open fun getAudioConfig(): AudioConfig {
        return AudioConfig(MediaRecorder.AudioSource.DEFAULT,
                DEFAULT_SAMPLE_RATE,
                DEFAULT_SAMPLE_DURATION_MS,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT)
    }

    private fun startProcessing() {
        if (processingStarted) {
            Logger.warn("processing is already started. skip")

            return
        }

        val audioConfig = getAudioConfig()

        recordingBufferLock.lock()
        recordingBuffer = ShortArray(audioConfig.getRecordingBufferLength())
        recordingOffset = 0
        recordingBufferLock.unlock()

        recordingThread.start()
        processThread.start()

        processingStarted = true
    }

    private fun stopProcessing() {
        if (!processingStarted) {
            Logger.warn("processing is already stopped. skip")

            return
        }

        processingStarted = false

        recordingThread.stop()
        processThread.stop()
    }

    private var recordingThread = object : ManagedThread() {

        override fun runInBackground() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)

            val audioConfig = getAudioConfig()

            // Estimate the buffer size we'll need for this device.
            var bufferSize = AudioRecord.getMinBufferSize(audioConfig.sampleRate,
                    audioConfig.channelConfig,
                    audioConfig.audioFormat)

            if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                bufferSize = audioConfig.sampleRate * 2
            }

            val audioBuffer = ShortArray(bufferSize / 2)

            val record = AudioRecord(
                    audioConfig.audioSource,
                    audioConfig.sampleRate,
                    audioConfig.channelConfig,
                    audioConfig.audioFormat,
                    bufferSize
            )

            if (record.state != AudioRecord.STATE_INITIALIZED) {
                Logger.error("audio record initialization failed: state = ${record.state}")
                return
            }

            record.startRecording()

            // Loop, gathering audio data and copying it to a round-robin buffer.
            while (isRunning()) {
                val numberRead = record.read(audioBuffer, 0, audioBuffer.size)

                val maxLength: Int = recordingBuffer.size
                val newRecordingOffset: Int = recordingOffset + numberRead
                val secondCopyLength = max(0, newRecordingOffset - maxLength)
                val firstCopyLength = numberRead - secondCopyLength

                // We store off all the data for the recognition thread to access. The ML
                // thread will copy out of this buffer into its own, while holding the
                // lock, so this should be thread safe.
                recordingBufferLock.lock()
                try {
                    System.arraycopy(audioBuffer, 0,
                        recordingBuffer, recordingOffset, firstCopyLength)
                    System.arraycopy(audioBuffer, firstCopyLength,
                        recordingBuffer, 0, secondCopyLength)

                    recordingOffset = newRecordingOffset % maxLength
                } finally {
                    recordingBufferLock.unlock()
                }
            }

            Logger.info("stop recording: record = $record")

            record.stop()
            record.release()
        }
    }

    private var processThread = object : ManagedThread() {

        override fun runInBackground() {
            val audioConfig = getAudioConfig()
            val inputBuffer = ShortArray(audioConfig.getRecordingBufferLength())

            while (isRunning()) {
                recordingBufferLock.lock()

                try {
                    val maxLength = recordingBuffer.size
                    val firstCopyLength = maxLength - recordingOffset
                    val secondCopyLength = recordingOffset
                    System.arraycopy(recordingBuffer, recordingOffset,
                            inputBuffer, 0, firstCopyLength)
                    System.arraycopy(recordingBuffer, 0,
                            inputBuffer, firstCopyLength, secondCopyLength)
                } finally {
                    recordingBufferLock.unlock()
                }

                Logger.debug("new input buffer ready: ${inputBuffer.size} Bytes")

                visualizer?.let {
                    lifecycleScope.launch(Dispatchers.Main) {
                        it.setAudioFrameData(inputBuffer)
                    }
                }

                onProcessAudioData(audioConfig, inputBuffer)

                try {
                    // We don't need to run too frequently, so snooze for a bit.
                    Thread.sleep(MINIMUM_TIME_BETWEEN_SAMPLES_MS)
                } catch (e: InterruptedException) {
                    // Ignore
                }
            }
        }
    }

    abstract fun onProcessAudioData(audioConfig: AudioConfig,
                                    audioData: ShortArray)

}