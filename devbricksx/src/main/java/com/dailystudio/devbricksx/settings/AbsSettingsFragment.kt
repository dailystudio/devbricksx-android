package com.dailystudio.devbricksx.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dailystudio.devbricksx.R

abstract class AbsSettingsFragment: Fragment() {

    private var settingsView: SettingsView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_settings, null)

        setupViews(view)

        return view
    }

    private fun setupViews(fragmentView: View) {
        settingsView = fragmentView.findViewById(
                R.id.settings_view)

        reloadSettings(requireContext())
    }

    protected open fun reloadSettings(context: Context) {
        val settings: Array<AbsSetting> = createSettings(context)
        settingsView?.setSettings(settings)
    }

    open fun addSetting(setting: AbsSetting) {
        settingsView?.addSetting(setting)
    }

    protected abstract fun createSettings(context: Context): Array<AbsSetting>

}