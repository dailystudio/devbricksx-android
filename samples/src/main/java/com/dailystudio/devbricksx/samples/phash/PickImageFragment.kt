package com.dailystudio.devbricksx.samples.phash

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dailystudio.devbricksx.fragment.PickFilesFragment
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.phash.viewmodel.PHashViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class PickImageFragment: PickFilesFragment() {

    abstract val imageIndex: Int

    private var imageView: ImageView? = null
    private var hashValueView: TextView? = null

    lateinit var viewModel: PHashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireParentFragment())[PHashViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.pHashValues[imageIndex].collectLatest {
                    hashValueView?.text = it
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pick_image, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.picked_image)
        hashValueView = view.findViewById(R.id.hash_value)

        val pickButton: View? = view.findViewById(R.id.pick_button)
        pickButton?.setOnClickListener {
            openFilePicker()
        }
    }

    override fun onFilesPicked(listOfUri: Array<Uri>?) {
        val imageUri = listOfUri?.first() ?: return
        imageView?.setImageURI(imageUri)

        viewModel.setImage(imageIndex, imageUri)
    }

}

class PickImage1Fragment: PickImageFragment() {
    override val imageIndex: Int
        get() = 0
}

class PickImage2Fragment: PickImageFragment() {
    override val imageIndex: Int
        get() = 1
}