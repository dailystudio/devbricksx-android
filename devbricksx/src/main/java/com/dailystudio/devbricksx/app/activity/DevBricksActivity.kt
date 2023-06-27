package com.dailystudio.devbricksx.app.activity

import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import com.google.android.material.snackbar.Snackbar

open class DevBricksActivity : AppCompatActivity() {

    private var snackBar: Snackbar? = null

    fun showFragment(fragmentId: Int) {
        showFragment(fragmentId, 0)
    }

    fun showFragment(fragmentId: Int, enterAnim: Int) {
        showFragment(findFragment(fragmentId), enterAnim)
    }

    fun showFragment(fragment: Fragment?) {
        showFragment(fragment, 0)
    }

    fun showFragment(fragment: Fragment?, enterAnim: Int) {
        if (fragment == null || fragment.isVisible) {
            return
        }

        val ft =
            supportFragmentManager.beginTransaction()
        if (enterAnim > 0) {
            ft.setCustomAnimations(enterAnim, 0)
        }

        ft.show(fragment)
        ft.commitAllowingStateLoss()
    }

    fun hideFragment(fragmentId: Int) {
        hideFragment(fragmentId, 0)
    }

    fun hideFragment(fragmentId: Int, enterAnim: Int) {
        hideFragment(findFragment(fragmentId), enterAnim)
    }

    fun hideFragment(fragment: Fragment?) {
        hideFragment(fragment, 0)
    }

    fun hideFragment(fragment: Fragment?, exitAnim: Int) {
        if (fragment == null || !fragment.isVisible) {
            return
        }

        val ft =
            supportFragmentManager.beginTransaction()
        if (exitAnim > 0) {
            ft.setCustomAnimations(0, exitAnim)
        }

        ft.hide(fragment)
        ft.commitAllowingStateLoss()
    }

    fun hideFragmentOnCreate(fragmentId: Int) {
        hideFragmentOnCreate(findFragment(fragmentId))
    }

    fun hideFragmentOnCreate(fragment: Fragment?) {
        if (fragment == null) {
            return
        }
        val ft =
            supportFragmentManager.beginTransaction()
        ft.hide(fragment)
        ft.commit()
    }

    fun isFragmentVisible(fragmentId: Int): Boolean {
        return isFragmentVisible(findFragment(fragmentId))
    }

    fun isFragmentVisible(fragment: Fragment?): Boolean {
        return fragment?.isVisible ?: false
    }

    fun findFragment(fragmentId: Int): Fragment? {
        val frgmgr = supportFragmentManager ?: return null
        return frgmgr.findFragmentById(fragmentId)
    }

    open fun showPrompt(prompt: CharSequence,
                        duration: Int = Snackbar.LENGTH_INDEFINITE,
                        @ColorInt textColor: Int? = null,
                        @ColorInt backgroundColor: Int? = null,
                        anchorView: View? = null) {
        val contextView: View = anchorView
            ?: findViewById(android.R.id.content)
            ?: window.decorView

        if (snackBar != null) {
            hidePrompt()
        }

        snackBar = Snackbar.make(contextView,
            prompt, duration).apply {

            textColor?.let {
                setTextColor(it)
            }

            val bgColorTint = backgroundColor ?: ResourcesCompatUtils.getColor(
                this@DevBricksActivity, R.color.snack_bar_bg_color)
            setBackgroundTint(bgColorTint)

            Logger.debug("snack bar shown: %s", this)
        }.also {
            it.show()
        }
    }

    open fun updatePrompt(prompt: CharSequence) {
        snackBar?.let {
            if (it.isShownOrQueued) {
                it.setText(prompt)
            }
        }
    }

    open fun hidePrompt() {
        snackBar?.let {
            it.dismiss()
            Logger.debug("snack bar dismissed: %s", it)
        }
        snackBar = null
    }

}