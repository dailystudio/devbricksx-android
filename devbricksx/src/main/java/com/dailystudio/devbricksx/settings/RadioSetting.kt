package com.dailystudio.devbricksx.settings

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.widget.TextViewCompat
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger

interface RadioSettingItem {

    fun getId(): String
    fun getLabel(): CharSequence

}


data class SimpleRadioSettingItem(private val context: Context,
                                  private val itemId: String,
                                  private val labelResId: Int) : RadioSettingItem {

    override fun getId(): String {
        return itemId
    }

    override fun getLabel(): CharSequence {
        return context.getString(labelResId)
    }

}

abstract class RadioSetting<T : RadioSettingItem>(context: Context,
                                                  name: String,
                                                  iconResId: Int,
                                                  labelResId: Int,
                                                  items: Array<T>,
                                                  enabled: Boolean = true,
                                                  holder: RadioSettingHolder = RadioSettingHolder())
    : AbsSetting(context, name, iconResId, labelResId, enabled, holder) {

    private val items: MutableList<T> = ArrayList()
    private val lock = Object()

    init {
        addItems(items)
    }

    fun addItem(item: T) {
        synchronized(lock) { items.add(item) }
        postInvalidate()
    }

    fun addItems(items: Array<T>) {
        if (items.isEmpty()) {
            return
        }

        synchronized(lock) {
            for (item in items) {
                this.items.add(item)
            }
        }

        postInvalidate()
    }

    fun clear() {
        synchronized(lock) { items.clear() }
        postInvalidate()
    }

    fun getItem(position: Int): T {
        return items[position]
    }

    val itemCount: Int
        get() = items.size

    fun findItemById(itemId: String): T? {
        if (TextUtils.isEmpty(itemId)) {
            return null
        }
        for (item in items) {
            if (itemId == item.getId()) {
                return item
            }
        }
        return null
    }

    abstract val selectedId: String?

    abstract fun setSelected(selectedId: String?)

}


class RadioSettingHolder: AbsSettingHolder() {

    private var mRadioGroup: RadioGroup? = null

    override fun onCreateView(context: Context, layoutInflater: LayoutInflater, setting: AbsSetting): View {
        return layoutInflater.inflate(R.layout.layout_setting_radio, null)
    }

    override fun invalidate(context: Context, setting: AbsSetting) {
        mRadioGroup?.removeAllViews()
        if (setting is RadioSetting<*>) {
            bindRadios(context, mRadioGroup, setting)
        }
    }

    override fun bindSetting(settingView: View, setting: AbsSetting) {
        super.bindSetting(settingView, setting)

        if (setting !is RadioSetting<*>) {
            return
        }

        mRadioGroup = settingView.findViewById(
                R.id.selection_group)

        bindRadios(setting.context, mRadioGroup, setting)
    }

    private fun bindRadios(context: Context,
                           radioGroup: RadioGroup?,
                           radioSetting: RadioSetting<*>) {
        if (radioSetting.itemCount <= 0) {
            return
        }

        val selectedId: String? = radioSetting.selectedId
        var checkedId = -1
        var item: RadioSettingItem
        var rb: RadioButton
        val rbs = arrayOfNulls<RadioButton>(radioSetting.itemCount)
        for (i in 0 until radioSetting.itemCount) {
            item = radioSetting.getItem(i)

            rb = RadioButton(context)
            rb.text = item.getLabel()
            TextViewCompat.setTextAppearance(rb, R.style.SettingsText)
            rb.tag = item

            rb.setOnCheckedChangeListener { compoundButton, isChecked ->
                Logger.debug("radio $compoundButton: checked = $isChecked")
                if (isChecked && compoundButton != null) {
                    val o = compoundButton.tag
                    if (o is RadioSettingItem) {
                        radioSetting.setSelected(o.getId())
//                        radioSetting.postInvalidate()
                        radioSetting.notifySettingChange()
                    }
                }
            }

            radioGroup?.addView(rb)

            if (!TextUtils.isEmpty(selectedId)
                    && selectedId == item.getId()) {
                checkedId = rb.id
            }

            rbs[i] = rb
        }

        Logger.debug("checkedId = $checkedId")
        if (checkedId != -1) {
            radioGroup?.check(checkedId)
        }

    }
}

