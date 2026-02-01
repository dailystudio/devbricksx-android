package com.dailystudio.devbricksx.app.activity

import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dailystudio.devbricksx.R
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import com.google.android.material.snackbar.Snackbar

/**
 * Base Activity class for DevBricksX based activities.
 *
 * It provides utility methods for fragment management and UI prompts (Snackbars).
 */
open class DevBricksActivity : AppCompatActivity() {

    private var snackBar: Snackbar? = null

    /**
     * Shows a fragment by its ID.
     *
     * @param fragmentId The ID of the fragment to show.
     */
    fun showFragment(fragmentId: Int) {
        showFragment(fragmentId, 0)
    }

    /**
     * Shows a fragment by its ID with a custom enter animation.
     *
     * @param fragmentId The ID of the fragment to show.
     * @param enterAnim The resource ID of the enter animation.
     */
    fun showFragment(fragmentId: Int, enterAnim: Int) {
        showFragment(findFragment(fragmentId), enterAnim)
    }

    /**
     * Shows a fragment instance.
     *
     * @param fragment The fragment instance to show.
     */
    fun showFragment(fragment: Fragment?) {
        showFragment(fragment, 0)
    }

    /**
     * Shows a fragment instance with a custom enter animation.
     *
     * @param fragment The fragment instance to show.
     * @param enterAnim The resource ID of the enter animation.
     */
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

    /**
     * Hides a fragment by its ID.
     *
     * @param fragmentId The ID of the fragment to hide.
     */
    fun hideFragment(fragmentId: Int) {
        hideFragment(fragmentId, 0)
    }

    /**
     * Hides a fragment by its ID with a custom exit animation.
     *
     * @param fragmentId The ID of the fragment to hide.
     * @param enterAnim The resource ID of the exit animation (Note: parameter name `enterAnim` in original code seems to be used as `exitAnim` logic in implementation or just passed through, checking implementation: `hideFragment(findFragment(fragmentId), enterAnim)` calls `hideFragment(fragment: Fragment?, exitAnim: Int)`).
     */
    fun hideFragment(fragmentId: Int, enterAnim: Int) {
        hideFragment(findFragment(fragmentId), enterAnim)
    }

    /**
     * Hides a fragment instance.
     *
     * @param fragment The fragment instance to hide.
     */
    fun hideFragment(fragment: Fragment?) {
        hideFragment(fragment, 0)
    }

    /**
     * Hides a fragment instance with a custom exit animation.
     *
     * @param fragment The fragment instance to hide.
     * @param exitAnim The resource ID of the exit animation.
     */
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

    /**
     * Hides a fragment by its ID immediately (e.g., in onCreate).
     *
     * @param fragmentId The ID of the fragment to hide.
     */
    fun hideFragmentOnCreate(fragmentId: Int) {
        hideFragmentOnCreate(findFragment(fragmentId))
    }

    /**
     * Hides a fragment instance immediately (e.g., in onCreate).
     *
     * @param fragment The fragment instance to hide.
     */
    fun hideFragmentOnCreate(fragment: Fragment?) {
        if (fragment == null) {
            return
        }
        val ft =
            supportFragmentManager.beginTransaction()
        ft.hide(fragment)
        ft.commit()
    }

    /**
     * Checks if a fragment is visible.
     *
     * @param fragmentId The ID of the fragment.
     * @return True if visible, false otherwise.
     */
    fun isFragmentVisible(fragmentId: Int): Boolean {
        return isFragmentVisible(findFragment(fragmentId))
    }

    /**
     * Checks if a fragment instance is visible.
     *
     * @param fragment The fragment instance.
     * @return True if visible, false otherwise.
     */
    fun isFragmentVisible(fragment: Fragment?): Boolean {
        return fragment?.isVisible ?: false
    }

    /**
     * Finds a fragment by its ID.
     *
     * @param fragmentId The ID of the fragment.
     * @return The fragment instance, or null if not found.
     */
    fun findFragment(fragmentId: Int): Fragment? {
        val frgmgr = supportFragmentManager ?: return null
        return frgmgr.findFragmentById(fragmentId)
    }

    /**
     * Shows a prompt message using a Snackbar.
     *
     * @param prompt The message to display.
     * @param duration The duration to show the message. Defaults to [Snackbar.LENGTH_INDEFINITE].
     * @param textColor The color of the message text.
     * @param backgroundColor The background color of the Snackbar.
     * @param anchorView The view to anchor the Snackbar to.
     */
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

    /**
     * Updates the text of the currently shown prompt.
     *
     * @param prompt The new message text.
     */
    open fun updatePrompt(prompt: CharSequence) {
        snackBar?.let {
            if (it.isShownOrQueued) {
                it.setText(prompt)
            }
        }
    }

    /**
     * Hides the currently shown prompt.
     */
    open fun hidePrompt() {
        snackBar?.let {
            it.dismiss()
            Logger.debug("snack bar dismissed: %s", it)
        }
        snackBar = null
    }

}