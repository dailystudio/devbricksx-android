package com.dailystudio.devbricksx.gallery

import android.os.Bundle
import androidx.core.view.WindowCompat
import com.dailystudio.devbricksx.app.activity.DevBricksActivity

class MainActivity : DevBricksActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_main)
    }

}