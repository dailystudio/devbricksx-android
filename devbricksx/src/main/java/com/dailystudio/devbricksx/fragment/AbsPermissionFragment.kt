package com.dailystudio.devbricksx.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.ArrayUtils

abstract class AbsPermissionsFragment : Fragment() {

    companion object {

        private const val REQUEST_PERMISSIONS = 10

        fun hasPermissions(context: Context,
                           permissions: Array<String>): Boolean {
            if (permissions.isEmpty()) {
                return true
            }

            var granted: Int
            for (perm in permissions) {
                granted = ContextCompat.checkSelfPermission(
                        context, perm)
                if (granted != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }

            return true
        }
    }

    private var mRootView: View? = null
    private var mPromptView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions(requireContext(), getRequiredPermissions())) {
            requestPermissions()
        } else {
            onPermissionsGranted(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_permissions, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRootView = view.findViewById(R.id.fragment_view_root)
        mRootView?.setOnClickListener { requestPermissions() }

        mPromptView = view.findViewById(android.R.id.empty)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {
            Logger.debug("permissions = [%s], grantResults = [%s]",
                    ArrayUtils.stringArrayToString(permissions),
                    ArrayUtils.intArrayToString(grantResults))
            if (isAllPermissionsGranted(grantResults)) {
                // Take the user to the success fragment when permission is granted
                Logger.warn("All of required permissions are granted")

                onPermissionsGranted(true)
            } else {
                mPromptView?.visibility = View.VISIBLE

                Logger.warn("Permissions request denied")
                onPermissionsDenied()
            }
        }
    }

    private fun isAllPermissionsGranted(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) {
            return false
        }

        for (gr in grantResults) {
            if (gr != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    private fun requestPermissions() {
        if (mPromptView != null) {
            mPromptView!!.visibility = View.GONE
        }

        Logger.debug("request required permissions")

        requestPermissions(getRequiredPermissions(), REQUEST_PERMISSIONS)
    }

    abstract fun getRequiredPermissions(): Array<String>

    abstract fun onPermissionsGranted(newlyGranted: Boolean)
    abstract fun onPermissionsDenied()

}