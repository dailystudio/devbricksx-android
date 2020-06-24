package com.dailystudio.devbricksx.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.ArrayUtils

abstract class AbsPermissionsFragment : Fragment() {

    companion object {

        private const val REQUEST_PERMISSIONS = 553

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

    private var mPromptView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasPermissions(requireContext(), getRequiredPermissions())) {
            requestPermissions()
        } else {
            onPermissionsGranted(false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val promptViewId = getPermissionsPromptViewId()
        if (promptViewId > 0) {
            mPromptView = view.findViewById(promptViewId)
        }

        mPromptView?.setOnClickListener { requestPermissions() }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {
            Logger.debug("permissions = [%s], grantResults = [%s]",
                    ArrayUtils.stringArrayToString(permissions),
                    ArrayUtils.intArrayToString(grantResults))
            if (isAllPermissionsGranted(grantResults)) {
                Logger.warn("All of required permissions are granted")
                mPromptView?.visibility = View.GONE

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
        Logger.debug("request required permissions")

        requestPermissions(getRequiredPermissions(), REQUEST_PERMISSIONS)
    }

    protected fun isPermissionsGranted(): Boolean {
        return hasPermissions(requireContext(), getRequiredPermissions())
    }

    abstract fun getPermissionsPromptViewId(): Int
    abstract fun getRequiredPermissions(): Array<String>
    abstract fun onPermissionsGranted(newlyGranted: Boolean)
    abstract fun onPermissionsDenied()

}