package com.dailystudio.devbricksx.samples.jackandjill.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.network.lan.Jack
import com.dailystudio.devbricksx.network.lan.Jill
import com.dailystudio.devbricksx.network.lan.JillCmdHandler
import com.dailystudio.devbricksx.network.lan.JillInfo
import com.dailystudio.devbricksx.samples.common.RandomNames
import com.dailystudio.devbricksx.samples.jackandjill.MyJill
import com.dailystudio.devbricksx.samples.jackandjill.MyJillManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class JillExt(
    id: String,
    val name: String
): Jill(id = id) {
    override fun handleRequest(request: String): String {
        return when (request) {
            "name" -> { name }

            else -> {
                buildString {
                    append("[Jill Echo]:")
                    append(request)
                }
            }
        }
    }
}

class MyJillViewModelExt(application: Application): MyJillViewModel(application) {


    val jillId = System.currentTimeMillis().toString()
    val jillName = RandomNames.nextName()

    private val jill = object: JillExt(id = jillId, name = jillName) {
        override fun handleRequest(request: String): String {
            _jillRequest.postValue(request)
            return super.handleRequest(request)
        }
    }

    private val _jillRequest: MutableLiveData<String> = MutableLiveData()
    val jillRequest: LiveData<String> = _jillRequest

    private val jack = Jack(ignores = listOf(jillId), scope = viewModelScope)

    private val jillsObserver = Observer<List<JillInfo>> {
        Logger.debug("new jills arrived: $it")

        MyJillManager.clear()

        it.forEach { jillInfo ->
            val myJill = MyJill(
                id = jillInfo.serviceName
            ).apply {
                name = buildString {
                    append(jillInfo.serviceIp)
                    append(",")
                    append(jillInfo.servicePort)
                }
            }

            addJill(myJill)
        }
    }

    init {
        jack.jills.observeForever(jillsObserver)

        jack.discover(application)
        jill.online(application)
    }

    override fun onCleared() {
        jack.jills.removeObserver(jillsObserver)

        jill.offline()
        jack.stopDiscover()
    }

    fun addJill(jill: MyJill) {
        insertMyJill(jill)

        viewModelScope.launch(Dispatchers.IO) {
            val name = jack.askJill(jill.id, "name")

            name?.let {
                jill.name = name
                updateMyJill(jill)
            }
        }
    }

    fun askJill(jill: MyJill, request: String) {
        viewModelScope.launch(Dispatchers.IO) {
            jack.askJill(jill.id, request)
        }
    }
}