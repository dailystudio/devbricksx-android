package com.dailystudio.devbricksx.samples.paging.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.fragment.AbsRecyclerViewFragment
import com.dailystudio.devbricksx.samples.paging.RedditPost
import com.dailystudio.devbricksx.samples.paging.model.RedditPostViewModel
import com.dailystudio.devbricksx.samples.paging.ui.RedditPostsAdapter

open class RedditPostsListFragment : AbsRecyclerViewFragment<RedditPost, PagedList<RedditPost>,
    RedditPostsAdapter>() {
  override fun onCreateAdapter(): RedditPostsAdapter = RedditPostsAdapter()

  override fun submitData(adapter: RedditPostsAdapter, data: PagedList<RedditPost>) {
    adapter.submitList(data)
    adapter.notifyDataSetChanged()
  }

  override fun onCreateLayoutManager(): LayoutManager = LinearLayoutManager(context)

  override fun getLiveData(): LiveData<PagedList<RedditPost>> {
    val viewModel = ViewModelProvider(this).get(RedditPostViewModel::class.java)
    return viewModel.allRedditPostsPaged
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = inflater.inflate(R.layout.fragment_recycler_view, container, false)
}
