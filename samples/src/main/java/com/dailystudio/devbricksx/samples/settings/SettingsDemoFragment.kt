package com.dailystudio.devbricksx.samples.settings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieAnimationView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.settings.Settings

class SettingsDemoFragment : Fragment() {

    companion object {

        private const val DURATION_CHECK_INTERVAL = 500L

    }

    private var demoTextView: TextView? = null
    private var demoTextCard: CardView? = null
    private var demoAnimation: LottieAnimationView? = null
    private var demoAttribution: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings_demo, null)

        setupViews(view)

        return view
    }

    private fun setupViews(fragmentView: View) {
        demoTextView = fragmentView.findViewById(R.id.demo_text)
        demoTextCard = fragmentView.findViewById(R.id.demo_text_card)
        demoAnimation = fragmentView.findViewById(R.id.demo_animation)
        demoAttribution = fragmentView.findViewById(R.id.demo_attribution)

        syncText()
        syncRoundedCorner()
        syncTextStyle()
        syncAnimation()
        syncAttribution()

        Settings.observe(viewLifecycleOwner, Observer {
            when (it.name) {
                SampleSettingsPrefs.PREF_ROUNDED_CORNER,
                SampleSettingsPrefs.PREF_CORNER_RADIUS -> {
                    syncRoundedCorner()
                }
                TextStyleSettingsPrefs.PREF_TEXT_STYLE,
                TextStyleSettingsPrefs.PREF_MAX_LINES -> {
                    syncTextStyle()
                }
                TextSettingsPrefs.PREF_TEXT_INPUT -> {
                    syncText()
                }
                SampleSettingsPrefs.PREF_ANIM_DURATION -> {
                    syncAnimation()
                }
                SampleSettingsPrefs.PREF_DISPLAY_ATTRIBUTION -> {
                    syncAttribution()
                }
            }
        })
    }

    private fun syncAnimation() {
        checkAndSyncAnimationSpeed()
    }

    private fun completeCheckAndSyncAnimationSpeed() {
        handler.removeCallbacks(checkDurationAndSyncSpeedRunnable)
    }

    private fun delayCheckAndSyncAnimationSpeed() {
        handler.postDelayed(checkDurationAndSyncSpeedRunnable, DURATION_CHECK_INTERVAL)
    }

    private fun checkAndSyncAnimationSpeed() {
        val animationView = demoAnimation ?: return

        val duration = animationView.duration
        if (duration == 0L) {
            delayCheckAndSyncAnimationSpeed()

            return
        }

        applyAnimSpeed(animationView, duration)

        completeCheckAndSyncAnimationSpeed()
    }

    private fun applyAnimSpeed(animationView: LottieAnimationView,
                               origDuration: Long) {
        var targetDuration = SampleSettingsPrefs.instance.animDuration
        if (targetDuration <= 0) {
            targetDuration = SampleSettings.MIN_ANIM_DURATION
        }

        val speed = origDuration / targetDuration.toFloat()

        animationView.speed = speed
    }

    private fun syncText() {
        val defaultText = getString(R.string.default_demo_text)

        val text = SampleSettingsPrefs.instance.textInput
        demoTextView?.text = text?.let {
            if (it.isBlank()) {
                defaultText
            } else {
                it
            }
        } ?: defaultText

        Logger.debug("duration = ${demoAnimation?.duration}")

    }

    private fun syncRoundedCorner() {
        demoTextCard?.radius = if (SampleSettingsPrefs.instance.roundedCorner) {
            SampleSettingsPrefs.instance.cornerRadius
        } else {
            0f
        }
    }

    private fun syncAttribution() {
        demoAttribution?.visibility = if (SampleSettingsPrefs.instance.displayAttribution) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun syncTextStyle() {
        val textStyle = SampleSettingsPrefs.instance.textStyle
        Logger.debug("text style: $textStyle")

        val styleResId = when (textStyle) {
            TextStyleSettings.TEXT_STYLE_NORMAL -> R.style.DemoTextNormal
            TextStyleSettings.TEXT_STYLE_ITALIC-> R.style.DemoTextItalic
            TextStyleSettings.TEXT_STYLE_BOLD -> R.style.DemoTextBold
            TextStyleSettings.TEXT_STYLE_ITALIC_BOLD -> R.style.DemoTextItalicBold
            else -> R.style.DemoTextNormal
        }

        Logger.debug("styleResId: $styleResId")

        demoTextView?.maxLines = SampleSettingsPrefs.instance.maxLines
        demoTextView?.let {
            TextViewCompat.setTextAppearance(it, styleResId)
        }
    }

    private val checkDurationAndSyncSpeedRunnable: Runnable = Runnable {
        checkAndSyncAnimationSpeed()
    }

    private val handler = Handler(Looper.getMainLooper())
}