package com.dailystudio.devbricksx.samples.compose

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dailystudio.devbricksx.app.activity.DevBricksActivity
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.usecase.compose.UseCasesScreen
import com.dailystudio.devbricksx.samples.usecase.model.UseCaseViewModelExt

class MainActivity : DevBricksActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SamplesTheme {
                UseCasesScreen(
                    dataSource = @Composable {
                        val viewModel = viewModel<UseCaseViewModelExt>()
                        val data by viewModel.allUseCasesFlow.collectAsState(emptyList())
                        data
                    },
                    onItemClick = {
                        Logger.debug("click on case: $it")
                    }
                )
            }
        }

    }

}

