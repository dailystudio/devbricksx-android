package com.dailystudio.devbricksx.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dailystudio.devbricksx.development.Logger
import kotlinx.android.synthetic.main.fragment_recorder.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

open class RecorderFragment : AbsAudioFragment() {

    companion object {

        private const val DEFAULT_RECORDED_FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    }

    private var recordButton: View? = null

    private var recorder: MediaRecorder? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_recorder, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews(view)
    }

    private fun setupViews(fragmentView: View) {
        recordButton = fragmentView.findViewById(R.id.record_button)
        recordButton?.setOnClickListener{ view ->
            startRecording()
        }
    }

    override fun onPause() {
        super.onPause()

        stopRecording()
    }

    protected fun startRecording() {
        recorder = MediaRecorder().apply {

            val fileName = getMediaFileName()

            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Logger.error("recorder prepare() failed: $e")
            }

            start()
            Logger.info("recording audio into file: [$fileName]")
        }
    }

    protected fun stopRecording() {
        recorder?.apply {

            stop()
            release()
        }
        recorder = null
    }

    protected open fun getMediaFileName(): String {
        val outputDirectory = getOutputDirectory(requireContext())
        return File(outputDirectory,
                SimpleDateFormat(DEFAULT_RECORDED_FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis()) + ".3gp").toString()
    }

    protected open fun getOutputDirectory(context: Context): File? {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }


    override fun onPermissionsGranted(newlyGranted: Boolean) {
        recordButton?.isEnabled = true
    }

    override fun onPermissionsDenied() {
        super.onPermissionsDenied()

        recordButton?.isEnabled = false
    }

}