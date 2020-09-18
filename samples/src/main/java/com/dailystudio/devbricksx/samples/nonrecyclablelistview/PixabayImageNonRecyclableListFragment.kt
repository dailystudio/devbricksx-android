package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.fragment.AbsNonRecyclableListViewFragment
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.nonrecyclablelistview.model.PixabayImageViewModel
import com.dailystudio.devbricksx.samples.nonrecyclablelistview.ui.PixabayImagesAdapter

class PixabayImageNonRecyclableListFragment : AbsNonRecyclableListViewFragment<PixabayImage,
        List<PixabayImage>, PixabayImagesAdapter>() {

    override fun getLiveData(): LiveData<List<PixabayImage>> {
        val viewModel = ViewModelProvider(this).get(PixabayImageViewModel::class.java)
        return viewModel.allPixabayImagesLive
    }

    override fun submitData(adapter: PixabayImagesAdapter,
                            data: List<PixabayImage>) {
        adapter.submitList(data)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateAdapter(): PixabayImagesAdapter {
        return PixabayImagesAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_non_recyclable_list_view, container, false)

}