package com.dailystudio.devbricksx.camera

import android.Manifest
import com.dailystudio.devbricksx.fragment.AbsPermissionsFragment


class CameraPermissionsFragment: AbsPermissionsFragment() {

    override fun onPermissionsGranted(newlyGranted: Boolean) {
    }

    override fun onPermissionsDenied() {
    }

    override fun getRequiredPermissions(): Array<String> {
        return PERMISSIONS_REQUIRED
    }

    companion object {
        val PERMISSIONS_REQUIRED = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO)
    }

}