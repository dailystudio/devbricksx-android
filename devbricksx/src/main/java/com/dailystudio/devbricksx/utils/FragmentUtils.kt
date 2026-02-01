package com.dailystudio.devbricksx.utils

import android.app.Activity
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

private fun Fragment.getAppCompatActivity(): AppCompatActivity? {
    val activity = activity ?: return null
    if (activity !is AppCompatActivity) {
        return null
    }

    return (activity)
}

/**
 * Finds the ActionBar of the hosting Activity.
 *
 * @return The ActionBar, or null if the activity is not an AppCompatActivity or has no ActionBar.
 */
fun Fragment.findActionBar(): ActionBar? {
    return getAppCompatActivity()?.supportActionBar
}

/**
 * Changes the title of the ActionBar.
 *
 * @param title The new title.
 */
fun Fragment.changeTitle(title: CharSequence?) {
    val actionBar = findActionBar() ?: return

    actionBar.title = title
}

/**
 * Changes the title of the ActionBar using a resource ID.
 *
 * @param labelResId The resource ID of the new title.
 */
fun Fragment.changeTitle(labelResId: Int) {
    changeTitle(if (labelResId > 0) {
        getString(labelResId)
    } else {
        null
    })
}

/**
 * Changes the subtitle of the ActionBar.
 *
 * @param title The new subtitle.
 */
fun Fragment.changeSubTitle(title: CharSequence?) {
    val actionBar = findActionBar() ?: return

    actionBar.subtitle = title
}

/**
 * Changes the subtitle of the ActionBar using a resource ID.
 *
 * @param labelResId The resource ID of the new subtitle.
 */
fun Fragment.changeSubTitle(labelResId: Int) {
    changeSubTitle(if (labelResId > 0) {
        getString(labelResId)
    } else {
        null
    })
}
