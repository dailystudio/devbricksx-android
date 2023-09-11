package com.dailystudio.devbricksx.samples.apps

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.apps.model.TestPackageViewModel
import com.dailystudio.devbricksx.samples.common.BaseCaseFragment
import com.dailystudio.devbricksx.utils.AppChangesLiveData
import com.dailystudio.devbricksx.utils.AppUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CaseFragment : BaseCaseFragment() {

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

        const val STEP_DELAY = 0L
    }

    private lateinit var appChangesLiveData: AppChangesLiveData

    override val fragmentLayoutResId: Int
        get() = R.layout.fragment_case_apps

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appChangesLiveData = AppChangesLiveData(requireContext()).apply {
            observe(viewLifecycleOwner, Observer {
                Logger.debug("new app changes: $it")
                lifecycleScope.launch{
                    updatePackage(it.packageName)
                }
            })
        }

        runBlocking {
            generateTestPackages()
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            testInstallations()
            resolveIcons()
        }
    }

    private fun updatePackage(packageName: String) {
        val context = requireContext()
        Logger.debug("update package: $packageName")
        val viewModel = ViewModelProvider(this).get(TestPackageViewModel::class.java)

        val pkg = viewModel.getTestPackage(packageName)
        pkg?.let {
            pkg.icon = null
            pkg.installed = AppUtils.isApplicationInstalled(context, pkg.packageName)
            Logger.debug("update app [$pkg] installed: ${pkg.installed}")

            if (pkg.installed) {
                pkg.icon = AppUtils.getApplicationIcon(context, pkg.packageName)
                Logger.debug("update resolved icon for [$pkg]: ${pkg.icon}")
            }

            viewModel.updateTestPackage(pkg)
        }
    }

    private suspend fun testInstallations() {
        val context = requireContext()

        val viewModel = ViewModelProvider(this).get(TestPackageViewModel::class.java)

        val packages = viewModel.allTestPackages
        for (pkg in packages) {
            pkg.installed = AppUtils.isApplicationInstalled(context, pkg.packageName)
            Logger.debug("app [$pkg] installed: ${pkg.installed}")

            viewModel.updateTestPackage(pkg)

            delay(STEP_DELAY)
        }
    }

    private suspend fun resolveIcons() {
        val context = requireContext()

        val viewModel = ViewModelProvider(this).get(TestPackageViewModel::class.java)

        val packages = viewModel.allTestPackages
        for (pkg in packages) {
            pkg.icon = null
            if (pkg.installed) {
                pkg.icon = AppUtils.getApplicationIcon(context, pkg.packageName)
                Logger.debug("resolved icon for [$pkg]: ${pkg.icon}")
            }

            viewModel.updateTestPackage(pkg)

            delay(STEP_DELAY)
        }
    }

    private fun generateTestPackages() {
        val viewModel = ViewModelProvider(this).get(TestPackageViewModel::class.java)

        for ((label, pkg) in TEST_APPLICATIONS_PACKAGES) {
            val tp = TestPackage(pkg, label)

            viewModel.insertTestPackage(tp)
        }
    }

}
