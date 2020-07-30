package com.dailystudio.devbricksx.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger
import java.text.NumberFormat
import kotlin.math.roundToInt

abstract class SeekBarSetting(context: Context,
                              name: String,
                              iconResId: Int,
                              labelResId: Int,
                              enabled: Boolean = true,
                              holder: SeekBarSettingHolder = SeekBarSettingHolder())
    : AbsSetting(context, name, iconResId, labelResId, enabled, holder) {

    abstract fun getProgress(context: Context): Float
    abstract fun setProgress(context: Context, progress: Float)
    abstract fun getMinValue(context: Context): Float
    abstract fun getMaxValue(context: Context): Float
    abstract fun getStep(context: Context): Float

}

class SeekBarSettingHolder : AbsSettingHolder() {

    override fun onCreateView(context: Context, layoutInflater: LayoutInflater, setting: AbsSetting): View {
        return layoutInflater.inflate(
                R.layout.layout_setting_seek_bar, null)
    }

    override fun invalidate(context: Context, setting: AbsSetting) {
        val view: View = getView()
        if (setting !is SeekBarSetting) {
            return
        }

        val seekBarView: SeekBar? = view.findViewById(
                R.id.setting_seek_bar)
        syncProgressWithSetting(context, seekBarView, setting)
    }

    private fun syncProgressWithSetting(context: Context,
                                        seekBar: SeekBar?,
                                        seekBarSetting: SeekBarSetting) {
        val progress = seekBarSetting.getProgress(context)
        val step = seekBarSetting.getStep(context)
        val min = seekBarSetting.getMinValue(context)
        val max = seekBarSetting.getMaxValue(context)
        Logger.debug("prg = %f, [min: %f, max: %f, step: %f",
                progress, min, max, step)

        seekBar?.progress = ((progress - min) / step).roundToInt()
        seekBar?.max = ((max - min) / step).roundToInt()
    }

    override fun bindSetting(settingView: View, setting: AbsSetting) {
        super.bindSetting(settingView, setting)
        if (setting !is SeekBarSetting) {
            return
        }

        val seekValView = settingView.findViewById<View>(
                R.id.setting_seek_value) as TextView
        if (seekValView != null) {
            val prg = setting.getProgress(setting.context)
            val prgStr = NumberFormat.getInstance().format(prg.toDouble())
            seekValView.text = prgStr
        }
        val seekBarView: SeekBar? = settingView.findViewById(
                R.id.setting_seek_bar)
        if (seekBarView != null) {
            val context = setting.context

            syncProgressWithSetting(context, seekBarView, setting)
            seekBarView.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    val prg = (setting.getMinValue(context)
                            + progress * setting.getStep(context))
                    if (seekValView != null) {
                        seekValView.text = NumberFormat.getInstance().format(prg.toDouble())
                    }

                    setting.setProgress(context, prg)
                    setting.notifySettingChange()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
            })
        }
    }

}
