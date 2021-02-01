package com.dailystudio.devbricksx.samples.paging.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.paging.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.fragment.AbsRecyclerViewFragment
import com.dailystudio.devbricksx.samples.paging.RedditPost
import com.dailystudio.devbricksx.samples.paging.RedditRemoteMediator
import com.dailystudio.devbricksx.samples.paging.model.RedditPostViewModel
import com.dailystudio.devbricksx.samples.paging.ui.RedditPostsAdapter

open class RedditPostsListFragment : AbsRecyclerViewFragment<RedditPost, PagingData<RedditPost>,
    RedditPostsAdapter>() {
  override fun onCreateAdapter(): RedditPostsAdapter = RedditPostsAdapter()

  override fun submitData(adapter: RedditPostsAdapter, data: PagingData<RedditPost>) {
    adapter.submitData(lifecycle, data)
    adapter.notifyDataSetChanged()
  }

  override fun onCreateLayoutManager(): LayoutManager = LinearLayoutManager(context)

  @OptIn(ExperimentalPagingApi::class)
  override fun getLiveData(): LiveData<PagingData<RedditPost>> {
    val viewModel = ViewModelProvider(this).get(RedditPostViewModel::class.java)
    return Pager(
            PagingConfig(/* pageSize = */ 20),
            remoteMediator = RedditRemoteMediator("androiddev")) {
              viewModel.postsBySubreddit("androiddev")
            }.flow.asLiveData()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_recycler_view, container, false)
}
