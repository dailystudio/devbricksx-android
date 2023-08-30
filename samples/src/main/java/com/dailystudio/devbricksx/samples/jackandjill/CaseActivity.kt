package com.dailystudio.devbricksx.samples.jackandjill

import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.network.lan.Jack
import com.dailystudio.devbricksx.network.lan.Jill
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.common.BaseCaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class CaseActivity : BaseCaseActivity() {

    private val jillId = System.currentTimeMillis().toString()
    private val jill = Jill(id = jillId)
    private val jack = Jack(ignores = listOf(jillId))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_case_jack_and_jill)

        setupViews()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                jack.jills.observe(this@CaseActivity) {
                    Logger.debug("new jills arrived: $it")

                    MyJillManager.clear()

                    it.forEach { jillInfo ->
                        MyJillManager.add(MyJill(
                            buildString {
                                append(jillInfo.first)
                                append(",")
                                append(jillInfo.second)
                            }
                        ))
                    }
                }
            }
        }
    }

    private fun setupViews() {
        val idView: TextView? = findViewById(R.id.jill_id)
        idView?.text = jillId.toString()
    }

    override fun onResume() {
        super.onResume()
        onlineJill()
        jackStart()
    }

    override fun onPause() {
        super.onPause()
        offlineJill()
        jackStop()
    }

    private fun onlineJill() {
        jill.online(this)
    }

    private fun offlineJill() {
        jill.offline()
    }

    private fun jackStart() {
        jack.discover(this, System.currentTimeMillis())
    }

    private fun jackStop() {
        jack.stopDiscover()
    }

}
