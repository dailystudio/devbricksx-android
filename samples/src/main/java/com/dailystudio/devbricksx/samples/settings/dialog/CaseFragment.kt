package com.dailystudio.devbricksx.samples.settings.dialog

import android.os.Bundle
import android.view.View
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseFragment

class CaseFragment : BaseCaseFragment() {

    override val fragmentLayoutResId: Int
        get() = R.layout.fragment_case_settings_dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsButton: View? = view.findViewById(R.id.button_show_settings)
        settingsButton?.setOnClickListener {
            val fragment = SampleSettingsDialogFragment()

            fragment.show(parentFragmentManager, "settings")
        }
    }

}
