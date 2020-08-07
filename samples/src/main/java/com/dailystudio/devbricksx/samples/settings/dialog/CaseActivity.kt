package com.dailystudio.devbricksx.samples.settings.dialog

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.settings.Settings

class CaseActivity : BaseCaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_settings_dialog)

        setupViews()
    }

    private fun setupViews() {
        val settingsButton: View? = findViewById(R.id.button_show_settings)
        settingsButton?.setOnClickListener {
            val fragment = SampleSettingsDialogFragment()

            fragment.show(supportFragmentManager, "settings")
        }
    }

}
