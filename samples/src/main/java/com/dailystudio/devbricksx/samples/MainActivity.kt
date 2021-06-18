package com.dailystudio.devbricksx.samples

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.app.activity.DevBricksActivity
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.fragment.AboutFragment
import com.dailystudio.devbricksx.samples.usecase.UseCase
import com.dailystudio.devbricksx.samples.usecase.UseCaseJsonDeserializer
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModel
import com.dailystudio.devbricksx.utils.JSONUtils
import com.dailystudio.devbricksx.utils.ResourcesCompatUtils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonDeserializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : DevBricksActivity() {

    companion object {

        const val SAMPLES_FILE = "samples.json"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        generateCases()

        showPrompt(getString(R.string.prompt_welcome),
            anchorView = findViewById(R.id.fragment_root),
        )

        lifecycleScope.launch() {
            delay(5000)
            hidePrompt()
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


    private fun generateCases() {
        lifecycleScope.launch(Dispatchers.IO) {
            val adapters: Map<Class<*>, JsonDeserializer<*>> =
                    mapOf(UseCase::class.java to UseCaseJsonDeserializer())
            val cases = JSONUtils.fromAsset(this@MainActivity,
                    SAMPLES_FILE,
                    Array<UseCase>::class.java,
                    adapters)
            Logger.debug("cases: $cases")

            cases?.let {
                val viewModel = ViewModelProvider(this@MainActivity)
                        .get(UseCaseViewModel::class.java)

                viewModel.insertUseCases(it.toList())
            }
        }
    }

}
