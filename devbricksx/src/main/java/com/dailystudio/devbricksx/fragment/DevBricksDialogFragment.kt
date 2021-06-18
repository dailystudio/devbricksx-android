package com.dailystudio.devbricksx.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.dailystudio.devbricksx.development.Logger

open class DevBricksDialogFragment : DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialog = dialog
        Logger.debug("dialog = %s", dialog)

        dialog?.setOnShowListener {
            val dialogInstance = getDialog()
            setupViewsOnDialog(dialogInstance)
        }

        bindIntent(requireActivity().intent)
    }

    open fun onNewIntent(intent: Intent?) {
        bindIntent(intent)
    }

    open protected fun setupViewsOnDialog(dialog: Dialog?) {}

    open fun bindIntent(intent: Intent?) {}

}
