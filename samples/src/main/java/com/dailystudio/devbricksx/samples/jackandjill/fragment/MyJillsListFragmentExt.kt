package com.dailystudio.devbricksx.samples.jackandjill.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.network.lan.Jack
import com.dailystudio.devbricksx.network.lan.Jill
import com.dailystudio.devbricksx.network.lan.JillCmdHandler
import com.dailystudio.devbricksx.samples.R
import com.dailystudio.devbricksx.samples.jackandjill.MyJill
import com.dailystudio.devbricksx.samples.jackandjill.MyJillManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyJillsListFragmentExt: MyJillsListFragment() {

    private val jillId = System.currentTimeMillis().toString()
    private val jill = Jill(id = jillId, handler = object : JillCmdHandler {
        override fun handleRequest(request: String): String {
            if (request == "name") {

            }
            return buildString {
                append("[Jill Echo]:")
                append(request)
            }
        }
    })

    private val jack = Jack(ignores = listOf(jillId), scope = lifecycleScope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                jack.jills.observe(this@MyJillsListFragmentExt) {
                    Logger.debug("new jills arrived: $it")

                    MyJillManager.clear()

                    it.forEach { jillInfo ->
                        MyJillManager.add(MyJill(
                            id = jillInfo.serviceName,
                            name = buildString {
                                append(jillInfo.serviceIp)
                                append(",")
                                append(jillInfo.servicePort)
                            }
                        ))
                    }
                }
            }
        }
    }

    override fun setupViews(fragmentView: View) {
        super.setupViews(fragmentView)

        val idView: TextView? = fragmentView.findViewById(R.id.jill_id)
        idView?.text = jillId
    }

    override fun onItemClick(
        recyclerView: RecyclerView,
        itemView: View,
        position: Int,
        item: MyJill,
        id: Long
    ) {
        super.onItemClick(recyclerView, itemView, position, item, id)
        lifecycleScope.launch(Dispatchers.IO) {
            jack.askJill(item.id, "Hi")
        }
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
        jill.online(requireContext())
    }

    private fun offlineJill() {
        jill.offline()
    }

    private fun jackStart() {
        jack.discover(requireContext(), System.currentTimeMillis())
    }

    private fun jackStop() {
        jack.stopDiscover()
    }

}