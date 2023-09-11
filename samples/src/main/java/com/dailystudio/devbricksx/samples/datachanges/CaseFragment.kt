package com.dailystudio.devbricksx.samples.datachanges

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseFragment
import com.dailystudio.devbricksx.samples.datachanges.model.ItemViewModel
import com.dailystudio.devbricksx.utils.StringUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class CaseFragment : BaseCaseFragment() {

    companion object {
        val RANDOM = Random(System.currentTimeMillis())
    }

    private lateinit var addItemsJob: Job
    private lateinit var updateItemsJob: Job
    override val fragmentLayoutResId: Int
        get() = R.layout.fragment_case_data_changes

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateItems()
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
        updateItemsJob = lifecycleScope.launchWhenResumed {
            addItemsJob.join()

            val viewModel = ViewModelProvider(this@CaseFragment).get(
                    ItemViewModel::class.java)

            while(true) {
                val items = viewModel.allItems
                if (items.isNotEmpty()) {
                    val pickedIndex = RANDOM.nextInt(items.size)

                    val item = items[pickedIndex]
                    item.count++
                    viewModel.updateItem(item)
                    Logger.debug("update item: $item")
                }

                delay(30)
            }
        }
    }

    private fun stopRandomModification() {
        updateItemsJob.cancel()
    }

    private fun generateItems() {
        val context = requireContext()

        addItemsJob = lifecycleScope.launch(Dispatchers.IO) {
            val viewModel = ViewModelProvider(this@CaseFragment).get(
                    ItemViewModel::class.java)
            val lines = StringUtils.linesFromAsset(context, "items.txt")
            for (l in lines) {
                val item = Item(l)

                viewModel.insertItem(item)
            }
        }
    }

}
