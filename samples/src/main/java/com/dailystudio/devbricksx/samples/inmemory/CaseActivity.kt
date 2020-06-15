package com.dailystudio.devbricksx.samples.inmemory

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.inmemory.model.CardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CaseActivity : BaseCaseActivity() {

    companion object {
        const val CARDS_COUNT = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_in_memory)

        generateCards()
    }

    private fun generateCards() {
        lifecycleScope.launch(Dispatchers.IO) {

            val viewModel = ViewModelProvider(this@CaseActivity).get(CardViewModel::class.java)

            for (i in 0 until CARDS_COUNT) {
                val card = Card(i,
                        "Card$i",
                        "This is a card with color blocks in it. If you display it in a card view, you will see only 3 lines of supporting text, whereas 6 lines in a informative card view.")

                viewModel.insertCard(card)

                delay(50)
            }
        }
    }

}
