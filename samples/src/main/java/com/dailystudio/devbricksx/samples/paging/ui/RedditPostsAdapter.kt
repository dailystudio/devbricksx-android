package com.dailystudio.devbricksx.samples.paging.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.samples.paging.RedditPost
import com.dailystudio.devbricksx.samples.paging.RedditPostDiffUtil
import com.dailystudio.devbricksx.samples.paging.RedditPostViewHolder
import com.dailystudio.devbricksx.ui.AbsPagedListAdapter
import com.dailystudio.devbricksx.ui.AbsPagingDataAdapter
import kotlin.Int

open class RedditPostsAdapter : AbsPagingDataAdapter<RedditPost, RedditPostViewHolder>(DIFF_CALLBACK)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedditPostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_single_line, null)
        return RedditPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: RedditPostViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    companion object {
        val DIFF_CALLBACK: ItemCallback<RedditPost> = RedditPostDiffUtil()
    }
}
