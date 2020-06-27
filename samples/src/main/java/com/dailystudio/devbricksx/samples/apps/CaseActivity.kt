package com.dailystudio.devbricksx.samples.apps

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.apps.model.TestPackageViewModel
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import com.dailystudio.devbricksx.utils.AppChangesLiveData
import com.dailystudio.devbricksx.utils.AppUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CaseActivity : BaseCaseActivity() {

    companion object {
        val TEST_APPLICATIONS_PACKAGES = mapOf<String, String>(
                "Play Store" to "com.android.vending",
                "Chrome" to "com.android.chrome",
                "Gmail" to "com.google.android.gm",
                "Maps" to "com.google.android.apps.maps",
                "WeChat" to "com.tencent.mm",
                "Skype" to "com.skype.raider",
                "Facebook" to "com.facebook.katana",
                "Twitter" to "com.twitter.android",
                "Telegram" to "org.telegram.messenger",
                "WhatsApp" to "com.whatsapp",
                "LinkIn" to "com.linkedin.android",
                "Evernote" to "com.evernote"
        )

        const val STEP_DELAY = 100L
    }

    private lateinit var appChangesLiveData: AppChangesLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_apps)

        lifecycleScope.launchWhenResumed {
            generateTestPackages()
            testInstallations()
            resolveIcons()
        }

        appChangesLiveData = AppChangesLiveData(this).apply {
            observe(this@CaseActivity, Observer {
                Logger.debug("new app changes: $it")
                lifecycleScope.launch{
                    testInstallations()
                    resolveIcons()
                }
            })
        }
    }

    private suspend fun testInstallations() {
        val viewModel = ViewModelProvider(this).get(TestPackageViewModel::class.java)

        val packages = viewModel.getTestPackages()
        for (pkg in packages) {
            pkg.installed = AppUtils.isApplicationInstalled(this, pkg.packageName)
            Logger.debug("app [$pkg] installed: ${pkg.installed}")

            viewModel.updateTestPackage(pkg).join()

            delay(STEP_DELAY)
        }
    }

    private suspend fun resolveIcons() {
        val viewModel = ViewModelProvider(this).get(TestPackageViewModel::class.java)

        val packages = viewModel.getTestPackages()
        for (pkg in packages) {
            if (pkg.installed) {
                pkg.icon = AppUtils.getApplicationIcon(this, pkg.packageName)
                Logger.debug("resolved icon for [$pkg]: ${pkg.icon}")
            }

            viewModel.updateTestPackage(pkg).join()

            delay(STEP_DELAY)
        }
    }

    private suspend fun generateTestPackages() {
        val viewModel = ViewModelProvider(this).get(TestPackageViewModel::class.java)

        for ((label, pkg) in TEST_APPLICATIONS_PACKAGES) {
            val tp = TestPackage(pkg, label)

            viewModel.insertTestPackage(tp).join()
        }
    }

}
