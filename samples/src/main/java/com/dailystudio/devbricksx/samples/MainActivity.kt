package com.dailystudio.devbricksx.samples

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.createGraph
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.fragment
import com.dailystudio.devbricksx.app.activity.DevBricksActivity
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.fragment.AboutFragment
import com.dailystudio.devbricksx.samples.usecase.fragment.UseCasesFragmentExt

class MainActivity : DevBricksActivity() {

    companion object {
        const val NAVIGATION_HOME = "home"
    }

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
            startDestination = NAVIGATION_HOME,
        ) {
            fragment<UseCasesFragmentExt>(NAVIGATION_HOME)
            fragment<com.dailystudio.devbricksx.samples.apps.CaseFragment>("apps")
            fragment<com.dailystudio.devbricksx.samples.audio.CaseFragment>("audio")
            fragment<com.dailystudio.devbricksx.samples.camera.CaseFragment>("camera")
            fragment<com.dailystudio.devbricksx.samples.customadapter.CaseFragment>("custom-adapter")
            fragment<com.dailystudio.devbricksx.samples.datachanges.CaseFragment>("data-changes")
            fragment<com.dailystudio.devbricksx.samples.fragmentpager.CaseFragment>("fragment-pager")
            fragment<com.dailystudio.devbricksx.samples.imagemask.CaseFragment>("image-mask")
            fragment<com.dailystudio.devbricksx.samples.imagematrix.CaseFragment>("image-matrix")
            fragment<com.dailystudio.devbricksx.samples.inmemory.CaseFragment>("in-memory")
            fragment<com.dailystudio.devbricksx.samples.jackandjill.CaseFragment>("jack-and-jill")
            fragment<com.dailystudio.devbricksx.samples.nonrecyclablelistview.CaseFragment>("non-recyclable-list-view")
            fragment<com.dailystudio.devbricksx.samples.paging.CaseFragment>("paging")
            fragment<com.dailystudio.devbricksx.samples.phash.CaseFragment>("phash")
            fragment<com.dailystudio.devbricksx.samples.quickstart.CaseFragment>("quick-start")
            fragment<com.dailystudio.devbricksx.samples.settings.dialog.CaseFragment>("settings-dialog")
            fragment<com.dailystudio.devbricksx.samples.settings.normal.CaseFragment>("settings-normal")
            fragment<com.dailystudio.devbricksx.samples.viewpager.CaseFragment>("viewpager")
            fragment<com.dailystudio.devbricksx.samples.webview.CaseFragment>("webview")
            fragment<com.dailystudio.devbricksx.samples.midi.channelview.CaseFragment>("midichannelview")
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
