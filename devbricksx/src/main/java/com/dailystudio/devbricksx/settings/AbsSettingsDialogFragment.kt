package com.dailystudio.devbricksx.settings

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.dailystudio.devbricksx.R

abstract class AbsSettingsDialogFragment: AppCompatDialogFragment() {

    private var settingsView: SettingsView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val dialogView: View = LayoutInflater.from(context).inflate(
                R.layout.fragment_settings_dialog, null)

        val topImageView: ImageView? = dialogView.findViewById(R.id.settings_top)
        if (topImageView != null) {
            val drawable: Drawable? = getSettingsTopImageDrawable()
            if (drawable != null) {
                topImageView.setImageDrawable(drawable)
                topImageView.visibility = View.VISIBLE
            } else {
                topImageView.setImageDrawable(null)
                topImageView.visibility = View.GONE
            }
        }

        settingsView = dialogView.findViewById(
                R.id.settings_view)

        reloadSettings(requireContext())


        val builder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok
                ) { _, _ -> }

        return builder.create()
    }

    protected open fun getSettingsTopImageDrawable(): Drawable? {
        return null
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