package com.dailystudio.devbricksx.audio

import android.Manifest
import com.dailystudio.devbricksx.fragment.AbsPermissionsFragment

abstract class AbsAudioFragment : AbsPermissionsFragment() {

    companion object {
        val PERMISSIONS_REQUIRED = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS)
    }

    override fun getPermissionsPromptViewId(): Int {
        return R.id.permission_prompt
    }

    override fun getRequiredPermissions(): Array<String> {
        return PERMISSIONS_REQUIRED
    }

    override fun onPermissionsDenied() {
    }

}