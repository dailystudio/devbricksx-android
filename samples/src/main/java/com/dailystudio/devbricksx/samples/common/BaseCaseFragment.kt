package com.dailystudio.devbricksx.samples.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import com.dailystudio.devbricksx.fragment.DevBricksFragment

abstract class BaseCaseFragment: DevBricksFragment() {

    abstract val fragmentLayoutResId: Int

    open val optionMenuResId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(fragmentLayoutResId, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (optionMenuResId == -1) {
            menu.clear()
        }
    }

}