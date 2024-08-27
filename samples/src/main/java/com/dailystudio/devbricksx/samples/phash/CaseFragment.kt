package com.dailystudio.devbricksx.samples.phash

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.core.R as coreR
import com.dailystudio.devbricksx.samples.common.BaseCaseFragment
import com.dailystudio.devbricksx.samples.phash.viewmodel.PHashViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CaseFragment : BaseCaseFragment() {

    override val fragmentLayoutResId: Int
        get() = R.layout.fragment_case_phash

    lateinit var viewModel: PHashViewModel

    private var similarityView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[PHashViewModel::class.java]

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.similarity.collectLatest {
                    similarityView?.text =
                        getString(coreR.string.prompt_similarity, it)

                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        similarityView = view.findViewById(R.id.similarity)
    }

    override fun onResume() {
        super.onResume()
    }


}
