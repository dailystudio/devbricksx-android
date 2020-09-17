package com.dailystudio.devbricksx.ui

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

open class NonRecyclableListView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var adapter: RecyclerView.Adapter<*>? = null

    private var selfScrollable: Boolean = false
    private var itemsContainer: ViewGroup? = null

    init {
        setupViews()

        attrs?.let {
            val a = context.obtainStyledAttributes(attrs,
                    R.styleable.NonRecyclableListView, defStyleAttr, 0)

            val selfScrollable = a.getBoolean(
                    R.styleable.NonRecyclableListView_self_scrollable,
                            false)

            setSelfScrollable(selfScrollable)

            a.recycle()
        }
    }

    private fun setupViews() {
        initLayout()
    }

    fun setSelfScrollable(scrollable: Boolean) {
        selfScrollable = scrollable

        initLayout()
    }

    private fun initLayout() {
        removeAllViews()

        LayoutInflater.from(context).inflate(if (selfScrollable) {
            R.layout.non_recyclable_list_view_self_scrollable
        } else {
            R.layout.non_recyclable_list_view
        }, this)

        itemsContainer = findViewById(R.id.items_container)
    }

    fun setAdapter(listAdapter: RecyclerView.Adapter<*>) {
        adapter?.unregisterAdapterDataObserver(mAdapterObserver)

        adapter = listAdapter
        adapter?.registerAdapterDataObserver(mAdapterObserver)

        appendItems()
    }

    fun getAdapter(): RecyclerView.Adapter<*>? {
        return adapter
    }

    private fun appendItems() {
        val adapter = adapter ?: return
        val container = itemsContainer?: return

        container.removeAllViews()

        val count = adapter.itemCount
        for (pos in 0 until count) {
            val type = adapter.getItemViewType(pos) ?: 0
            val viewHolder: RecyclerView.ViewHolder =
                adapter.onCreateViewHolder(this, type)

            container.addView(viewHolder.itemView)

            (adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
                    ).onBindViewHolder(viewHolder, pos)
        }
    }

    private val mAdapterObserver
            = object : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            super.onChanged()

            appendItems()
        }
    }

}