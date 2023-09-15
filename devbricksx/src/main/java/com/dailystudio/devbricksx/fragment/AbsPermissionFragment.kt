package com.dailystudio.devbricksx.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dailystudio.devbricksx.development.Logger

abstract class AbsPermissionsFragment : DevBricksFragment() {

    companion object {

        fun hasPermissions(context: Context,
                           permissions: Array<String>): Boolean {
            if (permissions.isEmpty()) {
                return true
            }

            var granted: Int
            for (perm in permissions) {
                granted = ContextCompat.checkSelfPermission(
                        context, perm)
                Logger.debug("[perm: $perm]: granted = ${granted == PackageManager.PERMISSION_GRANTED}")
                if (granted != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }

            return true
        }
    }

    private val singlePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            val permissions = getRequiredPermissions()
            if (permissions.isEmpty()) {
                return@registerForActivityResult
            }

            checkResults(mapOf(permissions[0] to it))

            return@registerForActivityResult
        }

    private val multiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            checkResults(it)
        }

    private fun checkResults(results: Map<String, Boolean>) {
        Logger.debug("results = $results")

        if (isAllPermissionsGranted(results)) {
            Logger.warn("All of required permissions are granted")
            mPromptView?.visibility = View.GONE

            onPermissionsGranted(true)
        } else {
            mPromptView?.visibility = View.VISIBLE

            Logger.warn("Permissions request denied")
            onPermissionsDenied()
        }
    }

    private var mPromptView: View? = null

    protected open val autoCheckPermissions: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (autoCheckPermissions) {
            checkOrGrantPermissions()
        }
    }

    fun checkOrGrantPermissions() {
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

    private fun isAllPermissionsGranted(grantResults: Map<String, Boolean>): Boolean {
        if (grantResults.isEmpty()) {
            return false
        }

        for (gr in grantResults.values) {
            if (!gr) {
                return false
            }
        }

        return true
    }

    private fun requestPermissions() {
        val permissions = getRequiredPermissions()
        Logger.debug("request required permissions: $permissions")

        if (permissions.isEmpty()) {
            return
        }

        if (permissions.size == 1) {
            singlePermissionLauncher.launch(permissions[0])
        } else {
            multiplePermissionsLauncher.launch(permissions)
        }
    }

    protected fun isPermissionsGranted(): Boolean {
        return hasPermissions(requireContext(), getRequiredPermissions())
    }

    abstract fun getPermissionsPromptViewId(): Int
    abstract fun getRequiredPermissions(): Array<String>
    abstract fun onPermissionsGranted(newlyGranted: Boolean)
    abstract fun onPermissionsDenied()

}