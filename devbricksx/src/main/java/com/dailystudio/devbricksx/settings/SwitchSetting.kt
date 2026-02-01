package com.dailystudio.devbricksx.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import com.dailystudio.devbricksx.R

/**
 * A setting that provides a toggle switch.
 */
abstract class SwitchSetting(context: Context,
                             name: String,
                             iconResId: Int,
                             labelResId: Int,
                             descResId: Int = -1,
                             enabled: Boolean = true,
                             holder: SwitchSettingLayoutHolder = SwitchSettingLayoutHolder())
    : TextSetting(context, name, iconResId, labelResId, descResId, enabled, holder) {

    /**
     * Checks if the switch is currently on.
     *
     * @return True if on, false otherwise.
     */
    abstract fun isOn(): Boolean

    /**
     * Sets the switch state.
     *
     * @param on True to turn on, false to turn off.
     */
    abstract fun setOn(on: Boolean)
}

/**
 * Holder for [SwitchSetting].
 */
open class SwitchSettingLayoutHolder : TextSettingLayoutHolder() {

    private var switch: SwitchCompat? = null

    override fun onCreateView(context: Context, layoutInflater: LayoutInflater, setting: AbsSetting): View {
        return layoutInflater.inflate(
                R.layout.layout_setting_switch, null)
    }

    override fun invalidate(context: Context, setting: AbsSetting) {
        if (setting is SwitchSetting) {
            switch?.isChecked = setting.isOn()
        }
    }

    override fun bindSetting(settingView: View, setting: AbsSetting) {
        super.bindSetting(settingView, setting)

        if (setting !is SwitchSetting) {
            return
        }

        switch = settingView.findViewById(R.id.setting_switch)
        if (switch != null) {
            val switchOn = setting.isOn()

            switch?.isChecked = switchOn
            switch?.setOnCheckedChangeListener { _, isChecked ->
                setting.setOn(isChecked)

                setting.notifySettingChange()
            }
        }
    }
}

