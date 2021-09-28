package com.dailystudio.devbricksx.samples.paging.fragment

import androidx.lifecycle.ViewModelProvider
import androidx.paging.*
import com.dailystudio.devbricksx.samples.paging.RedditPost
import com.dailystudio.devbricksx.samples.paging.RedditRemoteMediator
import com.dailystudio.devbricksx.samples.paging.model.RedditPostViewModel
import kotlinx.coroutines.flow.Flow

open class RedditPostsListFragmentExt : RedditPostsListFragment() {

  @ExperimentalPagingApi
  override fun getDataSource(): Flow<PagingData<RedditPost>> {
    val viewModel = ViewModelProvider(this).get(RedditPostViewModel::class.java)
    return Pager(
            PagingConfig(/* pageSize = */ 20),
            remoteMediator = RedditRemoteMediator("androiddev")) {
      viewModel.postsBySubreddit("androiddev")
    }.flow
  }

}
