package com.dailystudio.devbricksx.samples.imagematrix

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.fragment.AbsPageFragment
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.imagematrix.model.ImageBundleViewModel
import com.rasalexman.kdispatcher.*

fun Matrix.mapPointF(p: PointF) {
    var pts = floatArrayOf(p.x, p.y)
    mapPoints(pts)

    p.x = pts[0]
    p.y = pts[1]
}

data class EventTracksUpdate(val caller: Any, val tracks: List<List<PointF>>) {

    override fun toString(): String {
        return buildString {
            append("[$caller]: ")
            append("tracks = $tracks")
        }
    }

}

open class ImageBundleFragment(bundle: ImageBundle): AbsPageFragment<ImageBundle>(bundle) {

    companion object {

        const val EVENT_TRACKS_UPDATE = "tracks_update"


        fun getTransformedTracks(tracks: List<List<PointF>>,
                                 revertTransformation: Matrix): List<List<PointF>> {
            val transformedTracks = mutableListOf<List<PointF>>()

            for (track in tracks) {
                val rt = mutableListOf<PointF>()

                transformedTracks.add(rt)

                for (p in track) {
                    val mp = PointF(p.x, p.y)
                    revertTransformation.mapPointF(mp)

                    rt.add(mp)
                }

            }

            return transformedTracks
        }

    }

    private var drawingPad: DrawingPad? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(
                R.layout.fragment_drawing_pad, null)

        view?.let {
            setupViews(view)
        }

        return view
    }

    private fun setupViews(view: View) {
        drawingPad = view.findViewById(R.id.drawing_pad)

        drawingPad?.setImage(item.bitmap)
        drawingPad?.setTracksEditing(item.tracksEditing)
        drawingPad?.setTracks(item.tracks)
        drawingPad?.setOnTracksChangedListener(onTracksChangedListener)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        KDispatcher.subscribe(EVENT_TRACKS_UPDATE, 1, ::eventOneHandler)
    }

    override fun onDetach() {
        super.onDetach()

        KDispatcher.unsubscribe(EVENT_TRACKS_UPDATE, ::eventOneHandler)
    }

    private fun eventOneHandler(notification: Notification<EventTracksUpdate>) {
        println("${notification.eventName}: new tracks = ${notification.data}")

        val data = notification.data ?: return

        if (data.caller != this) {
            val tracks = data.tracks

            drawingPad?.setTracks(getTransformedTracks(
                    tracks, item.transformation))
            drawingPad?.setTracks(data.tracks)
        }
    }

    private val onTracksChangedListener = object: OnTracksChangedListener {

        override fun onTracksChanged(pad: DrawingPad, tracks: List<List<PointF>>) {
            val revertedTracks = getTransformedTracks(tracks, item.revertTransformation)

            val viewModel = ViewModelProvider(this@ImageBundleFragment).get(
                    ImageBundleViewModel::class.java)

            val bundles = viewModel.getImageBundles()
            for (bundle in bundles) {
                bundle.tracks = getTransformedTracks(revertedTracks, bundle.transformation)
            }

            val data = EventTracksUpdate(this@ImageBundleFragment,revertedTracks)

            KDispatcher.call(EVENT_TRACKS_UPDATE, data)
        }

    }

}