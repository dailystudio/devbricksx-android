package com.dailystudio.devbricksx.samples.settings

import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.settings.Settings

class CaseActivity : BaseCaseActivity() {

    private var demoTextView: TextView? = null
    private var demoTextCard: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_settings)

        setupViews()
    }

    private fun setupViews() {
        demoTextView = findViewById(R.id.demo_text)
        demoTextCard = findViewById(R.id.demo_text_card)
        syncRoundedCorner()

        Settings.observe(this, Observer {
            when (it.name) {
                SamplePrefs.PREF_ROUNDED_CORNER, SamplePrefs.PREF_CORNER_RADIUS-> {
                    syncRoundedCorner()
                }
            }
        })
    }

    private fun syncRoundedCorner() {
        val withRoundedCorner = SamplePrefs.withRoundedCorner(this)

        demoTextCard?.radius = if (withRoundedCorner) {
            SamplePrefs.getCornerRadius(this@CaseActivity)
        } else {
            0f
        }

    }

}
