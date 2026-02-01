package com.dailystudio.devbricksx.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dailystudio.devbricksx.R

/**
 * Abstract Fragment for displaying a list of settings.
 *
 * It uses [SettingsView] to render the settings.
 */
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

    /**
     * Reloads the settings and updates the view.
     *
     * @param context The context.
     */
    protected open fun reloadSettings(context: Context) {
        val settings: Array<AbsSetting> = createSettings(context)
        settingsView?.setSettings(settings)
    }

    /**
     * Adds a setting dynamically.
     *
     * @param setting The setting to add.
     */
    open fun addSetting(setting: AbsSetting) {
        settingsView?.addSetting(setting)
    }

    /**
     * Creates the list of settings to be displayed.
     *
     * @param context The context.
     * @return An array of [AbsSetting] objects.
     */
    protected abstract fun createSettings(context: Context): Array<AbsSetting>

}