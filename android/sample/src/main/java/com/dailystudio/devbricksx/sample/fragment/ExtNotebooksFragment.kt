package com.dailystudio.devbricksx.sample.fragment

import android.os.Bundle
import android.view.View
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.sample.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExtNotebooksFragment : NotebooksFragment() {

    var fab: FloatingActionButton? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab = view.findViewById(R.id.fab)
        fab?.setOnClickListener {
            Logger.debug("fab is clicked.")
        }
    }

}