package com.dailystudio.devbricksx.fragment

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Abstract dialog fragment for displaying an "About" screen.
 *
 * It shows the app icon, name, version, and description.
 */
abstract class AbsAboutFragment : DevBricksDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val dialogView:View = LayoutInflater.from(context).inflate(
            fragmentLayoutResource, null)

        setCustomizedView(dialogView)

        val builder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok
                ) { _, _ -> }

        return builder.create()
    }

    override fun setCustomizedView(view: View?) {
        super.setCustomizedView(view)

        val thumbView: View? = view?.findViewById(R.id.about_app_thumb)
        bindThumb(thumbView)

        val versionView: View? = view?.findViewById(R.id.about_app_ver)
        bindVersion(versionView)

        val nameView: View? = view?.findViewById(R.id.about_app_name)
        bindName(nameView)

        val descView: View? = view?.findViewById(R.id.about_app_desc)
        bindDesc(descView)

        val appIconView: View? = view?.findViewById(R.id.about_app_icon)
        bindIcon(appIconView)
    }

    /**
     * Binds the app thumbnail/banner to the view.
     *
     * @param view The thumbnail view.
     */
    protected open fun bindThumb(view: View?) {
        val thumbView = view as? ImageView ?: return

        val thumbResId = appThumbResource
        if (thumbResId <= 0) {
            thumbView.visibility = View.GONE
        } else {
            thumbView.visibility = View.VISIBLE
            thumbView.setImageResource(thumbResId)
        }
    }

    /**
     * Binds the app icon to the view.
     *
     * @param view The icon view.
     */
    protected open fun bindIcon(view: View?) {
        val appIconView = view as? ImageView ?: return

        appIconView.setImageResource(appIconResource)
    }

    /**
     * Binds the app version to the view.
     *
     * @param view The version text view.
     */
    protected open fun bindVersion(view: View?) {
        val versionView = view as? TextView ?: return
        val context = requireContext()

        lifecycleScope.launch(Dispatchers.IO) {
            val verName = AppUtils.getApplicationVersion(context, context.packageName)

            withContext(Dispatchers.Main) {
                versionView.text = verName
            }
        }
    }

    /**
     * Binds the app name to the view.
     *
     * @param view The name text view.
     */
    protected open fun bindName(view: View?) {
        val nameView = view as? TextView ?: return
        nameView.text = appName
    }

    /**
     * Binds the app description to the view.
     *
     * @param view The description text view.
     */
    protected open fun bindDesc(view: View?) {
        val descView = view as? TextView ?: return

        if (hasHtmlDescription()) {
            descView.autoLinkMask = 0
            descView.movementMethod = LinkMovementMethod.getInstance()
        } else {
            descView.autoLinkMask = Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS
        }

        descView.text = appDescription
    }

    /**
     * Checks if the description contains HTML content.
     *
     * @return True if HTML, false otherwise.
     */
    protected fun hasHtmlDescription(): Boolean {
        return false
    }

    /**
     * Gets the layout resource for the fragment.
     */
    protected open val fragmentLayoutResource: Int
        get() = R.layout.fragment_about

    /**
     * Gets the resource ID for the app thumbnail/banner.
     */
    protected open val appThumbResource: Int
        get() = -1

    /**
     * Gets the app name.
     */
    abstract val appName: CharSequence?

    /**
     * Gets the app description.
     */
    abstract val appDescription: CharSequence?

    /**
     * Gets the resource ID for the app icon.
     */
    abstract val appIconResource: Int

}
