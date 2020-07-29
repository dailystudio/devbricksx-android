package com.dailystudio.devbricksx.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger

abstract class AbsSettingsFragment: Fragment() {

    private var settingsContainer: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_settings, null)

        setupViews(view)

        return view
    }

    private fun setupViews(fragmentView: View) {
        settingsContainer = fragmentView.findViewById(
                R.id.settings_container)

        reloadSettings(requireContext())
    }

    protected open fun reloadSettings(context: Context) {
        settingsContainer?.removeAllViews()

        val settings: Array<AbsSetting> = createSettings(context)
        for (s in settings) {
            addSetting(s)

            s.syncEnabled()
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
    }

    protected abstract fun createSettings(context: Context): Array<AbsSetting>

}