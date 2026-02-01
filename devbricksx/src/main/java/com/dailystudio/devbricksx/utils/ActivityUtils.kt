package com.dailystudio.devbricksx.utils

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * Registers a Toolbar as the ActionBar for the activity.
 *
 * @param fragmentView The root view containing the toolbar.
 * @param actionResId The resource ID of the Toolbar.
 */
fun Activity.registerActionBar(fragmentView: View, actionResId: Int) {
    if (this is AppCompatActivity) {
        fragmentView.findViewById<Toolbar>(actionResId)?.let {
            setSupportActionBar(it)
        }
    }
}
