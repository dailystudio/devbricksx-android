package com.dailystudio.devbricksx.samples.usecase.composable

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Home() {
    val navController = rememberNavController()

    NavHost(navController = navController,
        startDestination = "usecases") {
        composable("usecases") {
            UseCasesScreenExt()
        }
        composable("usecase/{caseId}") {

        }
    }

}
