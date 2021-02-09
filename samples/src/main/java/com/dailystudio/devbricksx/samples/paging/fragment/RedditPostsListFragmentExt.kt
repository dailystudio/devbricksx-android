package com.dailystudio.devbricksx.samples.paging.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.paging.*
import com.dailystudio.devbricksx.samples.paging.RedditPost
import com.dailystudio.devbricksx.samples.paging.RedditRemoteMediator
import com.dailystudio.devbricksx.samples.paging.model.RedditPostViewModel

open class RedditPostsListFragmentExt : RedditPostsListFragment() {

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
