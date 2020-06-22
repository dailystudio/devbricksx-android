package com.dailystudio.devbricksx.samples.datachanges

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.datachanges.model.ItemViewModel
import com.dailystudio.devbricksx.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

class CaseActivity : BaseCaseActivity() {

    companion object {
        val RANDOM = Random(System.currentTimeMillis())
    }

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_data_changes)

        generateImages()
    }

    override fun onResume() {
        super.onResume()

        startRandomModification()
    }

    override fun onPause() {
        super.onPause()

        stopRandomModification()
    }

    private fun startRandomModification() {
        isRunning = true
        executor.execute {
            val viewModel = ViewModelProvider(this@CaseActivity).get(
                    ItemViewModel::class.java)

            while(isRunning) {
                val items = viewModel.getItems()
                if (items.isNotEmpty()) {
                    val pickedIndex = RANDOM.nextInt(items.size)

                    val item = items[pickedIndex]
                    item.count++
                    viewModel.updateItem(item)
                    Logger.debug("update item: $item")
                }

                Thread.sleep(30)
            }
        }
    }

    private fun stopRandomModification() {
        isRunning = false
        executor.shutdown()
    }

    private fun generateImages() {
        lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@CaseActivity).get(
                    ItemViewModel::class.java)
            val lines = StringUtils.linesFromAsset(this@CaseActivity, "items.txt")
            for (l in lines) {
                val item = Item(l)

                viewModel.insertItem(item)
            }
        }
    }

}
