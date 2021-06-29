package com.dailystudio.devbricksx.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.dailystudio.devbricksx.development.Logger

open class DevBricksDialogFragment : AppCompatDialogFragment() {

    private var customizedView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        customizedView?.let {
            setupCustomizedView(it)
        }

        return customizedView
    }

    override fun onDestroyView() {
        customizedView = null
        super.onDestroyView()
    }

    protected open fun setCustomizedView(view: View?) {
        customizedView = view
    }

    protected open fun getCustomizedView(): View? {
        return customizedView
    }

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

    protected open fun setupCustomizedView(view: View?) {}
    protected open fun setupViewsOnDialog(dialog: Dialog?) {}

    open fun bindIntent(intent: Intent?) {}

}
