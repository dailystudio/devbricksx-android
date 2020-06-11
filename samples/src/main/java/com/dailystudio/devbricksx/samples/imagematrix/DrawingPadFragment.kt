package com.dailystudio.devbricksx.samples.imagematrix

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dailystudio.devbricksx.samples.R

class DrawingPadFragment: Fragment() {

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
    }

    fun setImage(bitmap: Bitmap) {
        drawingPad?.setImage(bitmap)
    }

}