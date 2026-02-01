package com.dailystudio.devbricksx.settings

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.View
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils

/**
 * Data class representing a setting change event.
 *
 * @property name The name (key) of the setting.
 * @property timestamp The time of the change.
 */
data class SettingChange(val name: String,
                         val timestamp: Long = System.currentTimeMillis())

/**
 * Base class for a single setting item.
 *
 * Each setting has a name (key), an icon, a label, and an enabled state.
 * It is associated with a [AbsSettingHolder] to manage its view representation.
 * It implements [InMemoryObject] to be manageable by repositories.
 *
 * @property context The context.
 * @property name The name (key) of the setting.
 * @param iconResId The resource ID for the icon.
 * @param labelResId The resource ID for the label.
 * @property enabled The initial enabled state.
 * @property holder The holder responsible for rendering this setting.
 */
abstract class AbsSetting(val context: Context,
                          val name: String,
                          iconResId: Int,
                          labelResId: Int,
                          enabled: Boolean = true,
                          val holder: AbsSettingHolder): InMemoryObject<String> {

    companion object {

        private const val MINIMUM_INVALIDATE_INTERVAL = 300L

    }

    /**
     * The icon of the setting.
     */
    var icon: Drawable? = null

    /**
     * The label of the setting.
     */
    var label: CharSequence? = null

    /**
     * The enabled state of the setting.
     * Setting this updates the visibility of the setting's view.
     */
    var enabled = enabled
        set(enabled) {
            field = enabled
            syncEnabled()
        }

    init {
        setIcon(iconResId)
        setLabel(labelResId)
    }

    /**
     * Sets the icon resource ID.
     *
     * @param iconResId The resource ID.
     */
    fun setIcon(iconResId: Int) {
        icon = ResourcesCompatUtils.getDrawable(context, iconResId)
    }

    /**
     * Sets the label resource ID.
     *
     * @param labelResId The resource ID.
     */
    fun setLabel(labelResId: Int) {
        val res = context.resources ?: return

        label = res.getString(labelResId)
    }

    /**
     * Posts a request to invalidate the setting's view (redraw).
     * This is debounced by [MINIMUM_INVALIDATE_INTERVAL].
     */
    open fun postInvalidate() {
        mHandler.removeCallbacks(mInvalidateRunnable)
        mHandler.postDelayed(mInvalidateRunnable, MINIMUM_INVALIDATE_INTERVAL)
    }

    /**
     * Synchronizes the view visibility with the [enabled] state.
     */
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

    /**
     * Notifies listeners that this setting has changed.
     */
    fun notifySettingChange() {
        Logger.debug("notify setting: $name")
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
