package com.dailystudio.devbricksx.samples.paging.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.paging.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.fragment.AbsPagingRecyclerViewFragment
import com.dailystudio.devbricksx.fragment.AbsRecyclerViewFragment
import com.dailystudio.devbricksx.samples.paging.RedditPost
import com.dailystudio.devbricksx.samples.paging.RedditRemoteMediator
import com.dailystudio.devbricksx.samples.paging.model.RedditPostViewModel
import com.dailystudio.devbricksx.samples.paging.ui.RedditPostsAdapter
import kotlinx.coroutines.flow.Flow

open class RedditPostsListFragment : AbsPagingRecyclerViewFragment<RedditPost, LiveData<PagingData<RedditPost>>,
        RedditPostsAdapter>() {
  override fun onCreateAdapter(): RedditPostsAdapter = RedditPostsAdapter()

  override fun onCreateLayoutManager(): LayoutManager = LinearLayoutManager(context)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_recycler_view, container, false)

  @ExperimentalPagingApi
  override fun bindData() {
    val dataSource = getDataSource()
    dataSource.observe(viewLifecycleOwner, Observer { data ->
      adapter?.let {
        submitData(it, data)
      }
    })
  }

  @ExperimentalPagingApi
  override fun getDataSource(): LiveData<PagingData<RedditPost>> {
    val viewModel = ViewModelProvider(this).get(RedditPostViewModel::class.java)
    return Pager(
            PagingConfig(/* pageSize = */ 20),
            remoteMediator = RedditRemoteMediator("androiddev")) {
      viewModel.postsBySubreddit("androiddev")
    }.flow.asLiveData()
  }

}
