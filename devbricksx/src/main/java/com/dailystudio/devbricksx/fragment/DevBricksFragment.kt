package com.dailystudio.devbricksx.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * Base Fragment class for DevBricksX based fragments.
 *
 * It provides utility methods for child and parent fragment management, and intent handling.
 */
open class DevBricksFragment: Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindIntent(requireActivity().intent)
    }

    /**
     * Called when the hosting activity receives a new intent.
     *
     * @param intent The new intent.
     */
    open fun onNewIntent(intent: Intent?) {
        bindIntent(intent)
    }

    /**
     * Binds the intent to the fragment.
     *
     * This method is called in [onViewCreated] and [onNewIntent].
     * Override this method to extract data from the intent.
     *
     * @param intent The intent to bind.
     */
    open fun bindIntent(intent: Intent?) {}

    /**
     * Shows a child fragment by its ID.
     *
     * @param fragmentId The ID of the child fragment.
     */
    open fun showChildFragment(fragmentId: Int) {
        showChildFragment(fragmentId, 0)
    }

    /**
     * Shows a child fragment instance.
     *
     * @param fragment The child fragment instance.
     */
    open fun showChildFragment(fragment: Fragment?) {
        showChildFragment(fragment, 0)
    }

    /**
     * Shows a child fragment by its ID with a custom enter animation.
     *
     * @param fragmentId The ID of the child fragment.
     * @param enterAnim The resource ID of the enter animation.
     */
    open fun showChildFragment(fragmentId: Int, enterAnim: Int) {
        showChildFragment(findChildFragment(fragmentId), enterAnim)
    }

    /**
     * Shows a child fragment instance with a custom enter animation.
     *
     * @param fragment The child fragment instance.
     * @param enterAnim The resource ID of the enter animation.
     */
    open fun showChildFragment(
        fragment: Fragment?,
        enterAnim: Int
    ) {
        showFragment(fragment, enterAnim, true)
    }

    /**
     * Shows a sibling fragment by its ID (in the parent fragment manager).
     *
     * @param fragmentId The ID of the fragment.
     */
    open fun showFragment(fragmentId: Int) {
        showFragment(fragmentId, 0)
    }

    /**
     * Shows a sibling fragment instance (in the parent fragment manager).
     *
     * @param fragment The fragment instance.
     */
    open fun showFragment(fragment: Fragment?) {
        showFragment(fragment, 0)
    }

    /**
     * Shows a sibling fragment by its ID with a custom enter animation.
     *
     * @param fragmentId The ID of the fragment.
     * @param enterAnim The resource ID of the enter animation.
     */
    open fun showFragment(fragmentId: Int, enterAnim: Int) {
        showFragment(findFragment(fragmentId), enterAnim)
    }

    /**
     * Shows a sibling fragment instance with a custom enter animation.
     *
     * @param fragment The fragment instance.
     * @param enterAnim The resource ID of the enter animation.
     */
    open fun showFragment(fragment: Fragment?, enterAnim: Int) {
        showFragment(fragment, enterAnim, false)
    }

    private fun showFragment(
        fragment: Fragment?,
        enterAnim: Int,
        isChild: Boolean) {
        if (fragment == null || fragment.isVisible) {
            return
        }
        val fm =
            if (isChild) childFragmentManager else parentFragmentManager
        val ft = fm.beginTransaction()
        if (enterAnim > 0) {
            ft.setCustomAnimations(enterAnim, 0)
        }
        ft.show(fragment)
        ft.commitAllowingStateLoss()
    }

    /**
     * Hides a child fragment by its ID.
     *
     * @param fragmentId The ID of the child fragment.
     */
    open fun hideChildFragment(fragmentId: Int) {
        hideChildFragment(fragmentId, 0)
    }

    /**
     * Hides a child fragment instance.
     *
     * @param fragment The child fragment instance.
     */
    open fun hideChildFragment(fragment: Fragment?) {
        hideChildFragment(fragment, 0)
    }

    /**
     * Hides a child fragment by its ID with a custom exit animation.
     *
     * @param fragmentId The ID of the child fragment.
     * @param enterAnim The resource ID of the exit animation.
     */
    open fun hideChildFragment(fragmentId: Int, enterAnim: Int) {
        hideChildFragment(findChildFragment(fragmentId), enterAnim)
    }

    /**
     * Hides a child fragment instance with a custom exit animation.
     *
     * @param fragment The child fragment instance.
     * @param exitAnim The resource ID of the exit animation.
     */
    open fun hideChildFragment(
        fragment: Fragment?,
        exitAnim: Int
    ) {
        hideFragment(fragment, exitAnim, true)
    }

    /**
     * Hides a sibling fragment by its ID.
     *
     * @param fragmentId The ID of the fragment.
     */
    open fun hideFragment(fragmentId: Int) {
        hideFragment(fragmentId, 0)
    }

    /**
     * Hides a sibling fragment instance.
     *
     * @param fragment The fragment instance.
     */
    open fun hideFragment(fragment: Fragment?) {
        hideFragment(fragment, 0)
    }

    /**
     * Hides a sibling fragment by its ID with a custom exit animation.
     *
     * @param fragmentId The ID of the fragment.
     * @param enterAnim The resource ID of the exit animation.
     */
    open fun hideFragment(fragmentId: Int, enterAnim: Int) {
        hideFragment(findFragment(fragmentId), enterAnim)
    }

    /**
     * Hides a sibling fragment instance with a custom exit animation.
     *
     * @param fragment The fragment instance.
     * @param exitAnim The resource ID of the exit animation.
     */
    open fun hideFragment(fragment: Fragment?, exitAnim: Int) {
        hideFragment(fragment, exitAnim, false)
    }

    private fun hideFragment(
        fragment: Fragment?,
        exitAnim: Int,
        isChild: Boolean
    ) {
        if (fragment == null || !fragment.isVisible) {
            return
        }
        val fm =
            if (isChild) childFragmentManager else parentFragmentManager
        val ft = fm.beginTransaction()
        if (exitAnim > 0) {
            ft.setCustomAnimations(0, exitAnim)
        }
        ft.hide(fragment)
        ft.commit()
    }

    /**
     * Hides a child fragment by its ID immediately (e.g., in onCreate).
     *
     * @param fragmentId The ID of the child fragment.
     */
    open fun hideChildFragmentOnCreate(fragmentId: Int) {
        hideFragmentOnCreate(findChildFragment(fragmentId))
    }

    /**
     * Hides a child fragment instance immediately (e.g., in onCreate).
     *
     * @param fragment The child fragment instance.
     */
    open fun hideChildFragmentOnCreate(fragment: Fragment?) {
        hideFragmentOnCreate(fragment, true)
    }

    /**
     * Hides a sibling fragment by its ID immediately (e.g., in onCreate).
     *
     * @param fragmentId The ID of the fragment.
     */
    open fun hideFragmentOnCreate(fragmentId: Int) {
        hideFragmentOnCreate(findFragment(fragmentId))
    }

    /**
     * Hides a sibling fragment instance immediately (e.g., in onCreate).
     *
     * @param fragment The fragment instance.
     */
    open fun hideFragmentOnCreate(fragment: Fragment?) {
        hideFragmentOnCreate(fragment, false)
    }

    open fun hideFragmentOnCreate(
        fragment: Fragment?,
        isChild: Boolean
    ) {
        if (fragment == null) {
            return
        }
        val fm =
            if (isChild) childFragmentManager else parentFragmentManager
        val ft = fm.beginTransaction()
        ft.hide(fragment)
        ft.commit()
    }

    /**
     * Checks if a sibling fragment is visible.
     *
     * @param fragmentId The ID of the fragment.
     * @return True if visible, false otherwise.
     */
    open fun isFragmentVisible(fragmentId: Int): Boolean {
        return isFragmentVisible(findFragment(fragmentId))
    }

    /**
     * Checks if a child fragment is visible.
     *
     * @param fragmentId The ID of the child fragment.
     * @return True if visible, false otherwise.
     */
    open fun isChildFragmentVisible(fragmentId: Int): Boolean {
        return isFragmentVisible(findChildFragment(fragmentId))
    }

    /**
     * Checks if a fragment instance is visible.
     *
     * @param fragment The fragment instance.
     * @return True if visible, false otherwise.
     */
    open fun isFragmentVisible(fragment: Fragment?): Boolean {
        return fragment?.isVisible ?: false
    }

    /**
     * Finds a child fragment by its ID.
     *
     * @param fragmentId The ID of the child fragment.
     * @return The child fragment, or null if not found.
     */
    open fun findChildFragment(fragmentId: Int): Fragment? {
        val frgmgr = childFragmentManager ?: return null
        return frgmgr.findFragmentById(fragmentId)
    }

    /**
     * Finds a sibling fragment by its ID.
     *
     * @param fragmentId The ID of the fragment.
     * @return The fragment, or null if not found.
     */
    open fun findFragment(fragmentId: Int): Fragment? {
        val frgmgr = parentFragmentManager
        return frgmgr.findFragmentById(fragmentId)
    }

}