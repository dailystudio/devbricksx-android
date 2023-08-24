package com.dailystudio.devbricksx.samples

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.dailystudio.devbricksx.app.activity.DevBricksActivity
import com.dailystudio.devbricksx.samples.fragment.AboutFragment

class MainActivity : DevBricksActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                val fragment = AboutFragment()

                fragment.show(supportFragmentManager, "about")
            }
        }

        return super.onOptionsItemSelected(item)
    }

}
