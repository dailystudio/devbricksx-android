package com.dailystudio.devbricksx.samples.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.Constants
import com.dailystudio.devbricksx.samples.R

open class BaseCaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        val topBar: Toolbar? = findViewById(R.id.topAppBar)
        Logger.debug("topBar: $topBar")

        topBar?.let {
            setSupportActionBar(it)
        }

        val title = intent.getStringExtra(Constants.EXTRA_TITLE)
        Logger.debug("case title: $title")
        title?.let {
            topBar?.setTitle(title)
        }
    }

}