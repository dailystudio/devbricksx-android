package com.dailystudio.devbricksx.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dailystudio.devbricksx.development.Logger

/**
 * Listener for soft keyboard visibility changes.
 *
 * @param v The view observing the changes.
 * @param showingKeyboard True if keyboard is visible, false otherwise.
 */
typealias SoftKeyboardShowingListener = (v: View, showingKeyboard: Boolean) -> Unit

/**
 * Hides the soft keyboard associated with this view's window.
 */
fun View.hideKeyboard() {
    val imm: InputMethodManager? =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE)
                as InputMethodManager?
    imm?.hideSoftInputFromWindow(windowToken, 0);
}

/**
 * Adds a listener to detect soft keyboard visibility changes.
 *
 * @param listener The callback to invoke when visibility changes.
 */
fun View.addSoftKeyboardChangesListener(listener: SoftKeyboardShowingListener) {
    var lastShowingKeyboard = false

    viewTreeObserver.addOnGlobalLayoutListener {
        val rootWindowInsets = ViewCompat.getRootWindowInsets(rootView)
        val showingKeyboard =
            rootWindowInsets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
//        Logger.debug("[SOFT-KEY] showingKeyboard: $showingKeyboard")

        if (showingKeyboard != lastShowingKeyboard) {
            listener(this, showingKeyboard)
            lastShowingKeyboard = showingKeyboard
        }
    }
}