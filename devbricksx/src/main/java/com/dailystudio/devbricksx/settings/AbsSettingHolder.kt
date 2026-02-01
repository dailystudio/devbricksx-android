package com.dailystudio.devbricksx.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger

/**
 * Abstract class responsible for creating and binding the view for a [AbsSetting].
 */
abstract class AbsSettingHolder {

    private lateinit var mView: View

    /**
     * Creates the view for a setting and binds it.
     *
     * @param context The context.
     * @param setting The setting to visualize.
     * @return The created view.
     */
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

    /**
     * Gets the created view.
     *
     * @return The view.
     */
    fun getView(): View {
        return mView
    }

    /**
     * Binds common setting properties (icon, label) to the view.
     *
     * @param settingView The view to bind to.
     * @param setting The setting object.
     */
    protected open fun bindSetting(settingView: View, setting: AbsSetting) {
        Logger.debug("binding setting [$setting] to view [$settingView]")
        val iconView: ImageView? = settingView.findViewById(R.id.setting_icon)
        iconView?.setImageDrawable(setting.icon)

        val labelView: TextView? = settingView.findViewById(R.id.setting_label)
        labelView?.text = setting.label
    }

    /**
     * Invalidates the view to reflect changes in the setting.
     *
     * @param context The context.
     * @param setting The setting object.
     */
    abstract fun invalidate(context: Context, setting: AbsSetting)

    /**
     * Creates the view hierarchy for the setting.
     *
     * @param context The context.
     * @param layoutInflater The layout inflater.
     * @param setting The setting object.
     * @return The root view.
     */
    protected abstract fun onCreateView(context: Context,
                                        layoutInflater: LayoutInflater,
                                        setting: AbsSetting): View


}
