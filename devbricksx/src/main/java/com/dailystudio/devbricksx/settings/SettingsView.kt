package com.dailystudio.devbricksx.settings

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger

class SettingsView: FrameLayout {

    private var settingsContainer: ViewGroup? = null

    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(
                R.layout.layout_settings_view, this)

        setupViews()
    }

    private fun setupViews() {
        settingsContainer = findViewById(
                R.id.settings_container)
    }

    open fun setSettings(settings: Array<AbsSetting>) {
        settingsContainer?.removeAllViews()

        for (s in settings) {
            addSetting(s)
        }
    }

    open fun addSetting(setting: AbsSetting) {
        Logger.debug("add setting: $setting")
        val container = settingsContainer ?: return

        val settingHolder = setting.holder
        val view: View = settingHolder.createView(setting.context, setting)

        val lp = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        container.addView(view, lp)

        setting.syncEnabled()
    }

}