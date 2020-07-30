package com.dailystudio.devbricksx.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger

abstract class AbsSettingHolder {

    private lateinit var mView: View

    fun createView(context: Context, setting: AbsSetting): View {
        val layoutInflater = LayoutInflater.from(context)

        mView = onCreateView(context, layoutInflater, setting)
        Logger.debug("view created: $mView")

        bindSetting(mView, setting)
        mView.visibility = if (setting.enabled) {
            View.VISIBLE
        } else {
            View.GONE
        }

        return mView
    }

    fun getView(): View {
        return mView
    }

    protected open fun bindSetting(settingView: View, setting: AbsSetting) {
        Logger.debug("binding setting [$setting] to view [$settingView]")
        val iconView: ImageView? = settingView.findViewById(R.id.setting_icon)
        iconView?.setImageDrawable(setting.icon)

        val labelView: TextView? = settingView.findViewById(R.id.setting_label)
        labelView?.text = setting.label
    }

    abstract fun invalidate(context: Context, setting: AbsSetting)

    protected abstract fun onCreateView(context: Context,
                                        layoutInflater: LayoutInflater,
                                        setting: AbsSetting): View


}
