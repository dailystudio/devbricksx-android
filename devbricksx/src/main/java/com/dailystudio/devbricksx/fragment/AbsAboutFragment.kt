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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    protected open fun bindIcon(view: View?) {
        val appIconView = view as? ImageView ?: return

        appIconView.setImageResource(appIconResource)
    }

    protected open fun bindVersion(view: View?) {
        val versionView = view as? TextView ?: return
        val context = requireContext()

        lifecycleScope.launch(Dispatchers.IO) {
            var verName = getString(android.R.string.unknownName)

            val packageManager = context.packageManager
            val pkgInfo = try {
                packageManager.getPackageInfo(context.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                Logger.warn("could not get package info of current app: $e")

                null
            }

            pkgInfo?.let { info ->
                verName = info.versionName
            }

            withContext(Dispatchers.Main) {
                versionView.text = verName
            }
        }
    }

    protected open fun bindName(view: View?) {
        val nameView = view as? TextView ?: return
        nameView.text = appName
    }

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

    protected fun hasHtmlDescription(): Boolean {
        return false
    }

    protected open val fragmentLayoutResource: Int
        get() = R.layout.fragment_about

    protected open val appThumbResource: Int
        get() = -1

    abstract val appName: CharSequence?
    abstract val appDescription: CharSequence?
    abstract val appIconResource: Int

}
