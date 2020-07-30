package com.dailystudio.devbricksx.settings

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger

open class TextSetting(context: Context,
                       name: String,
                       iconResId: Int,
                       labelResId: Int,
                       descResId: Int = -1,
                       enabled: Boolean = true,
                       holder: TextSettingLayoutHolder = TextSettingLayoutHolder())
    : AbsSetting(context, name, iconResId, labelResId, enabled, holder) {
    var desc: CharSequence? = null

    init {
        setDesc(descResId)
    }

    fun setDesc(descResId: Int) {
        desc = if (descResId > 0) {
            context.resources.getString(descResId)
        } else {
            null
        }
    }

}

open class TextSettingLayoutHolder : AbsSettingHolder() {

    override fun onCreateView(context: Context,
                              layoutInflater: LayoutInflater,
                              setting: AbsSetting): View {
        return layoutInflater.inflate(
                R.layout.layout_setting_text, null)
    }

    override fun bindSetting(settingView: View, setting: AbsSetting) {
        super.bindSetting(settingView, setting)

        if (setting !is TextSetting) {
            return
        }

        val descView: TextView? = settingView.findViewById(R.id.setting_desc)
        descView?.text = setting.desc
        descView?.visibility = if (TextUtils.isEmpty(setting.desc)) {
            View.GONE
        } else {
            View.VISIBLE
        }

        val rootView: View? = settingView.findViewById(R.id.setting_root)
        rootView?.setOnClickListener {
            Logger.debug("clicked on text settings")
            setting.postInvalidate()
            setting.notifySettingChange()
        }
    }

    override fun invalidate(context: Context, setting: AbsSetting) {
    }

}