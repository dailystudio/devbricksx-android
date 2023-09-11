package com.dailystudio.devbricksx.samples

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.createGraph
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.fragment
import com.dailystudio.devbricksx.app.activity.DevBricksActivity
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.apps.CaseFragment
import com.dailystudio.devbricksx.samples.fragment.AboutFragment
import com.dailystudio.devbricksx.samples.usecase.fragment.UseCasesFragmentExt
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModelExt

object nav_routes {
    const val home = "home"
}

class MainActivity : DevBricksActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val topBar: Toolbar? = findViewById(R.id.topAppBar)
        Logger.debug("topBar: $topBar")

        topBar?.let {
            setSupportActionBar(it)
        }

        buildNaviGraph()
    }

    private fun buildNaviGraph() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) ?: return
        val navController = navHostFragment.findNavController()

        navController.graph = navController.createGraph(
            startDestination = nav_routes.home,
        ) {
            fragment<UseCasesFragmentExt>(nav_routes.home)
            fragment<com.dailystudio.devbricksx.samples.apps.CaseFragment>("apps")
            fragment<com.dailystudio.devbricksx.samples.audio.CaseFragment>("audio")
        }
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
