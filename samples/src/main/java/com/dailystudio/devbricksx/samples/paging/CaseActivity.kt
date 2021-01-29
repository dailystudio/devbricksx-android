package com.dailystudio.devbricksx.samples.paging

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.samples.nonrecyclablelistview.model.PixabayImageViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CaseActivity : BaseCaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_non_recyclable_list_view)
    }

}
