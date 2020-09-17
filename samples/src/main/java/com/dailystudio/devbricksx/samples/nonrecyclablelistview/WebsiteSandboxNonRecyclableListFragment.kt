package com.dailystudio.devbricksx.samples.nonrecyclablelistview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.fragment.AbsNonRecyclableListViewFragment
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.nonrecyclablelistview.model.WebsiteSandboxViewModel
import com.dailystudio.devbricksx.samples.nonrecyclablelistview.ui.WebsiteSandboxsAdapter

class WebsiteSandboxNonRecyclableListFragment : AbsNonRecyclableListViewFragment<WebsiteSandbox,
        List<WebsiteSandbox>, WebsiteSandboxsAdapter>() {

    override fun getLiveData(): LiveData<List<WebsiteSandbox>> {
        val viewModel = ViewModelProvider(this).get(WebsiteSandboxViewModel::class.java)
        return viewModel.allWebsiteSandboxsLive
    }

    override fun submitData(adapter: WebsiteSandboxsAdapter,
                            data: List<WebsiteSandbox>) {
        adapter.submitList(data)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateAdapter(): WebsiteSandboxsAdapter {
        return WebsiteSandboxsAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_non_recyclable_list_view, container, false)

}