package com.dailystudio.devbricksx.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.dailystudio.devbricksx.development.Logger

/**
 * Base DialogFragment for DevBricksX.
 *
 * It provides support for custom views and intent binding.
 */
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

    /**
     * Sets the custom view for the dialog.
     *
     * @param view The custom view.
     */
    protected open fun setCustomizedView(view: View?) {
        customizedView = view
    }

    /**
     * Gets the custom view of the dialog.
     *
     * @return The custom view.
     */
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

    /**
     * Called when the hosting activity receives a new intent.
     *
     * @param intent The new intent.
     */
    open fun onNewIntent(intent: Intent?) {
        bindIntent(intent)
    }

    /**
     * Sets up the custom view.
     *
     * @param view The custom view.
     */
    protected open fun setupCustomizedView(view: View?) {}

    /**
     * Sets up views when the dialog is shown.
     *
     * @param dialog The dialog instance.
     */
    protected open fun setupViewsOnDialog(dialog: Dialog?) {}

    /**
     * Binds intent data to the dialog.
     *
     * @param intent The intent.
     */
    open fun bindIntent(intent: Intent?) {}

}
