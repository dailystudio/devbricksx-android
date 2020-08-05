package com.dailystudio.devbricksx.samples.settings

import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Observer
import com.dailystudio.devbricksx.development.Logger
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

        syncText()
        syncRoundedCorner()
        syncTextStyle()

        Settings.observe(this, Observer {
            when (it.name) {
                SampleSettingsPrefs.PREF_ROUNDED_CORNER,
                SampleSettingsPrefs.PREF_CORNER_RADIUS -> {
                    syncRoundedCorner()
                }
                SampleSettingsPrefs.PREF_TEXT_STYLE,
                SampleSettingsPrefs.PREF_MAX_LINES -> {
                    syncTextStyle()
                }
                SampleSettingsPrefs.PREF_TEXT_INPUT -> {
                    syncText()
                }
            }
        })
    }

    private fun syncText() {
        val defaultText = getString(R.string.default_demo_text)

        val text = SampleSettingsPrefs.textInput
        demoTextView?.text = text?.let {
           if (it.isBlank()) {
                defaultText
           } else {
                it
           }
        } ?: defaultText
    }

    private fun syncRoundedCorner() {

        demoTextCard?.radius = if (SampleSettingsPrefs.roundedCorner) {
            SampleSettingsPrefs.cornerRadius
        } else {
            0f
        }
    }

    private fun syncTextStyle() {
        val textStyle = SampleSettingsPrefs.textStyle
        Logger.debug("text style: $textStyle")

        val styleResId = when (textStyle) {
            SampleSettings.TEXT_STYLE_NORMAL -> R.style.DemoTextNormal
            SampleSettings.TEXT_STYLE_ITALIC-> R.style.DemoTextItalic
            SampleSettings.TEXT_STYLE_BOLD -> R.style.DemoTextBold
            SampleSettings.TEXT_STYLE_ITALIC_BOLD -> R.style.DemoTextItalicBold
            else -> R.style.DemoTextNormal
        }

        Logger.debug("styleResId: $styleResId")

        demoTextView?.maxLines = SampleSettingsPrefs.maxLines
        demoTextView?.let {
            TextViewCompat.setTextAppearance(it, styleResId)
        }
    }

}
