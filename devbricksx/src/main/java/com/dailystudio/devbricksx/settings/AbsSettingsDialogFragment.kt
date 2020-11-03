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

        val thumbView: ImageView? = dialogView.findViewById(R.id.settings_dialog_thumb)
        if (thumbView != null) {
            val drawable: Drawable? = getDialogThumbImageDrawable()
            if (drawable != null) {
                thumbView.setImageDrawable(drawable)
                thumbView.visibility = View.VISIBLE
            } else {
                thumbView.setImageDrawable(null)
                thumbView.visibility = View.GONE
            }
        }

        settingsView = dialogView.findViewById(
                R.id.settings_view)

        val divider: View? = dialogView.findViewById(R.id.settings_divider)
        divider?.visibility = if (shouldDisplayDivider()) {
            View.VISIBLE
        } else {
            View.GONE
        }

        reloadSettings(requireContext())

        val builder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok
                ) { _, _ -> }

        return builder.create()
    }

    protected open fun getDialogThumbImageDrawable(): Drawable? {
        return null
    }

    protected open fun shouldDisplayDivider(): Boolean {
        return false
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