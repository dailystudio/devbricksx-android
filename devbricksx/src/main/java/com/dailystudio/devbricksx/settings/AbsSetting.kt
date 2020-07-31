package com.dailystudio.devbricksx.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

data class SettingChange(val name: String)

abstract class AbsSetting(val context: Context,
                          val name: String,
                          iconResId: Int,
                          labelResId: Int,
                          enabled: Boolean = true,
                          val holder: AbsSettingHolder): InMemoryObject<String> {

    companion object {

        private const val MINIMUM_INVALIDATE_INTERVAL = 300L

    }

    var icon: Drawable? = null
    var label: CharSequence? = null

    var enabled = enabled
        set(enabled) {
            field = enabled
            syncEnabled()
        }

    init {
        setIcon(iconResId)
        setLabel(labelResId)
    }

    fun setIcon(iconResId: Int) {
        icon = ResourcesCompatUtils.getDrawable(context, iconResId)
    }

    fun setLabel(labelResId: Int) {
        val res = context.resources ?: return

        label = res.getString(labelResId)
    }

    open fun postInvalidate() {
        mHandler.removeCallbacks(mInvalidateRunnable)
        mHandler.postDelayed(mInvalidateRunnable, MINIMUM_INVALIDATE_INTERVAL)
    }

    fun syncEnabled() {
        holder.let {
            val view = holder.getView()

            if (enabled) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }

    override fun getKey(): String {
        return name
    }

    fun notifySettingChange() {
        Logger.debug("notify setting: ${name}")
        Settings.postValue(SettingChange(name))
    }

    override fun toString(): String {
        return String.format("%s(0x%08x, enabled = %s): label = %s, icon = %s, holder = %s",
                javaClass.simpleName,
                hashCode(),
                enabled,
                label,
                icon,
                holder)
    }

    private val mInvalidateRunnable = Runnable {
        holder.invalidate(context, this@AbsSetting)
    }

    private val mHandler = Handler(Looper.getMainLooper())

}
