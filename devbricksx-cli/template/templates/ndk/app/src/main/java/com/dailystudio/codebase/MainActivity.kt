package com.dailystudio.codebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.dailystudio.codebase.core.R as coreR
import com.dailystudio.codebase.core.nativelib.NativeLib

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        val topBar: Toolbar? = findViewById(R.id.topAppBar)

        topBar?.let {
            setSupportActionBar(it)
        }

        val helloText: TextView? = findViewById(R.id.hello_text)
        val resId = coreR.string.hello

        helloText?.text = NativeLib().getString(this, resId)
    }

}
