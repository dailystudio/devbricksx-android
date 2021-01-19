package com.dailystudio.devbricksx.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.ImageView
import com.dailystudio.devbricksx.R


abstract class EditSetting(context: Context,
                           name: String,
                           iconResId: Int,
                           labelResId: Int,
                           enabled: Boolean = true,
                           holder: EditSettingHolder = EditSettingHolder())
    : AbsSetting(context, name, iconResId, labelResId, enabled, holder) {

    open fun getEditHint(context: Context): CharSequence? {
        return null
    }

    open fun getEditButtonDrawable(context: Context): Drawable? {
        return null
    }

    abstract fun getEditText(context: Context): CharSequence?
    abstract fun setEditText(context: Context, text: CharSequence?)
    abstract fun onEditButtonClicked(context: Context)

}

private data class TextChangeData(val setting: EditSetting,
                                  val text: CharSequence? = null)

open class EditSettingHolder : AbsSettingHolder() {

    companion object {
        private const val MSG_TEXT_CHANGED = 0x1
        private const val DELAY: Long = 500
    }

    private var editText: EditText? = null

    override fun onCreateView(context: Context, layoutInflater: LayoutInflater, setting: AbsSetting): View {
        return layoutInflater.inflate(
                R.layout.layout_setting_edit, null)
    }

    override fun invalidate(context: Context, setting: AbsSetting) {
        if (setting !is EditSetting) {
            return
        }

        val text = setting.getEditText(context)

        editText?.setText(text)

        text?.let {
            editText?.setSelection(it.length)
        }
    }

    override fun bindSetting(settingView: View, setting: AbsSetting) {
        super.bindSetting(settingView, setting)
        if (setting !is EditSetting) {
            return
        }

        val context = setting.context

        val editButton: ImageView? = settingView.findViewById(
                R.id.setting_edit_image_button)
        editButton?.let {
            val drawable = setting.getEditButtonDrawable(
                    context)
            if (drawable == null) {
                editButton.visibility = View.GONE
                editButton.setOnClickListener(null)
            } else {
                editButton.visibility = View.VISIBLE
                editButton.setImageDrawable(drawable)
                editButton.setOnClickListener {
                    setting.onEditButtonClicked(context)
                    setting.postInvalidate()
                }
            }
        }

        editText = settingView.findViewById(
                R.id.setting_edit)
        editText?.let {
            applyEditTextStyles(it)

            it.setText(setting.getEditText(context))
            it.hint = setting.getEditHint(context)
            it.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val editable = it.text

                    performEditTextChange(setting, editable)
                }
            }

            it.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    performEditTextChange(setting, s)
                }

            })
        }

    }

    protected open fun applyEditTextStyles(editText: EditText) {

    }

    private fun performEditTextChange(editSetting: EditSetting,
                                        s: CharSequence?) {
        mHandler.removeMessages(MSG_TEXT_CHANGED)

        val msg = Message.obtain(mHandler, MSG_TEXT_CHANGED,
                TextChangeData(editSetting, s))

        mHandler.sendMessageDelayed(msg, DELAY)
    }

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {

        override fun dispatchMessage(msg: Message) {
            if (msg.what == MSG_TEXT_CHANGED
                    && msg.obj is TextChangeData) {
                val data = msg.obj as TextChangeData
                val context = data.setting.context

                data.setting.setEditText(context, data.text)
//                data.setting.postInvalidate()
                data.setting.notifySettingChange()

                return
            }

            super.dispatchMessage(msg)
        }
    }

}
