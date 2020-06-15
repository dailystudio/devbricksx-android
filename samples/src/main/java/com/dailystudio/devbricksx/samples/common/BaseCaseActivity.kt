package com.dailystudio.devbricksx.samples.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.Constants

open class BaseCaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra(Constants.EXTRA_TITLE)
        Logger.debug("case title: $title")
        title?.let {
            setTitle(title)
        }
    }

}