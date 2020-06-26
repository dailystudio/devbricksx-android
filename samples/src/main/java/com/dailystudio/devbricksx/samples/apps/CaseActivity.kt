package com.dailystudio.devbricksx.samples.apps

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.apps.model.TestPackageViewModel
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.utils.AppUtils
import kotlinx.coroutines.runBlocking

class CaseActivity : BaseCaseActivity() {

    companion object {
        val TEST_APPLICATIONS_PACKAGES = arrayOf<String>(
                "com.google.mail",
                "com.google.calendar",
                "com.google.assistant",
                "com.google.translator"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_apps)

        runBlocking {
            generateTestPackages()
            testPackages()
        }
    }

    private fun testPackages() {
        for (pkg in TEST_APPLICATIONS_PACKAGES) {
            val installed = AppUtils.isPackageInstalled(this, pkg)
            Logger.debug("app [$pkg] installed: $installed")
        }
    }

    private suspend fun generateTestPackages() {
        val viewModel = ViewModelProvider(this).get(TestPackageViewModel::class.java)

        for (pkg in TEST_APPLICATIONS_PACKAGES) {
            val tp = TestPackage(pkg, pkg, pkg)

            viewModel.insertTestPackage(tp).join()
        }
    }

}
