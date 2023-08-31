package com.dailystudio.devbricksx.samples.jackandjill.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.jackandjill.model.MyJillViewModelExt

class MyJillsListFragmentExt: MyJillsListFragment() {

    private lateinit var viewModel: MyJillViewModelExt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MyJillViewModelExt::class.java]
    }

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)

        val idView: TextView? = fragmentView.findViewById(R.id.my_name)
        idView?.text = viewModel.jillName + "(" + viewModel.jillId + ")"
    }

}