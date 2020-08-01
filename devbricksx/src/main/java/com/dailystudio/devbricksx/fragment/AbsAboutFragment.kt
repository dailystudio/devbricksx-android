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
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger

abstract class AbsAboutFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val dialogView: View = LayoutInflater.from(context).inflate(
                R.layout.fragment_about, null)

        val thumbView: ImageView? = dialogView.findViewById(R.id.about_app_thumb)
        thumbView?.let {
            val thumbResId = appThumbResource
            if (thumbResId <= 0) {
                it.visibility = View.GONE
            } else {
                it.visibility = View.VISIBLE
                it.setImageResource(thumbResId)
            }
        }

        val versionView: TextView? = dialogView.findViewById(R.id.about_app_ver)
        versionView?.let {
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

            it.text = verName
        }

        val nameView: TextView? = dialogView.findViewById(R.id.about_app_name)
        nameView?.text = appName

        val descView: TextView? = dialogView.findViewById(R.id.about_app_desc)
        descView?.let {
            if (hasHtmlDescription()) {
                it.autoLinkMask = 0
                it.movementMethod = LinkMovementMethod.getInstance()
            } else {
                it.autoLinkMask = Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS
            }

            it.text = appDescription
        }

        val appIconView: ImageView? = dialogView.findViewById(R.id.about_app_icon)
        appIconView?.setImageResource(appIconResource)

        val builder = AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok
                ) { _, _ -> }

        return builder.create()
    }

    protected fun hasHtmlDescription(): Boolean {
        return false
    }

    protected open val appThumbResource: Int
        protected get() = -1

    abstract val appName: CharSequence?
    abstract val appDescription: CharSequence?
    abstract val appIconResource: Int

}
