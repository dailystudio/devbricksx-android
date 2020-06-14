package com.dailystudio.devbricksx.samples.fragmentpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.fragment.AbsViewPagerFragment
import com.dailystudio.devbricksx.samples.fragmentpager.model.ImageViewModel

open class ImagesPagerFragment : AbsViewPagerFragment<Image, List<Image>, ImageFragmentAdapter>() {

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)
        viewPager?.offscreenPageLimit = 2
    }

    override fun onCreateAdapter(): ImageFragmentAdapter = ImageFragmentAdapter(
            parentFragmentManager, lifecycle
    )

    override fun submitData(adapter: ImageFragmentAdapter, data: List<Image>) {
        adapter.submitList(data)
    }

    override fun getLiveData(): LiveData<List<Image>> {
        val viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        return viewModel.allImages
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_view_pager, container, false)
}
