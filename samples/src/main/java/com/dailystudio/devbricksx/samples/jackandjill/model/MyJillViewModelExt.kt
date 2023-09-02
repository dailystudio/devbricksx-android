package com.dailystudio.devbricksx.samples.jackandjill.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.network.lan.Jack
import com.dailystudio.devbricksx.network.lan.Jill
import com.dailystudio.devbricksx.network.lan.JillCmd
import com.dailystudio.devbricksx.network.lan.JillCmdResult
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

    override fun executeCommand(cmd: JillCmd): JillCmdResult? {
        return when (cmd.action) {
            "name" -> { JillCmdResult.okMessage(name) }

            else -> {
                JillCmdResult.okMessage(
                    "Got it"
                )

            }
        }
    }
}

class MyJillViewModelExt(application: Application): MyJillViewModel(application) {


    val jillId = System.currentTimeMillis().toString()
    val jillName = RandomNames.nextName()

    private val jill = object: JillExt(id = jillId, name = jillName) {
        override fun executeCommand(cmd: JillCmd): JillCmdResult? {
            _jillCmd.postValue(cmd)
            return super.executeCommand(cmd)
        }
    }

    private val _jillCmd: MutableLiveData<JillCmd> = MutableLiveData()
    val jillCmd: LiveData<JillCmd> = _jillCmd

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
            val ret = jack.askJill(jill.id, "name")

            ret.let {
                if (ret.code == JillCmdResult.STATUS_OK) {
                    jill.name = ret.getMessage()
                    updateMyJill(jill)
                }
            }
        }
    }

    fun askJill(jill: MyJill, request: String) {
        viewModelScope.launch(Dispatchers.IO) {
            jack.askJill(jill.id, request)
        }
    }
}