package com.dailystudio.devbricksx.samples.imagematrix

import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.imagematrix.model.ImageBundleViewModel

class DrawingPadFragment(bundle: ImageBundle): ImageBundleFragment(bundle) {

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

        Logger.debug("bitmap: ${item.bitmap}")
        drawingPad?.setImage(item.bitmap)
    }

    fun setImage(bitmap: Bitmap) {
        drawingPad?.setImage(bitmap)
    }

    private val onTracksChangedListener = object: OnTracksChangedListener {

        override fun onTracksChanged(pad: DrawingPad, tracks: List<List<PointF>>) {
            val viewModel = ViewModelProvider(this@DrawingPadFragment)
                    .get(ImageBundleViewModel::class.java)

            val bundles = viewModel.getImageBundles()
            for (bundle in bundles) {
            }
        }

    }

}