package com.dailystudio.devbricksx.samples.jackandjill.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.network.lan.Jack
import com.dailystudio.devbricksx.network.lan.JackAndJill
import com.dailystudio.devbricksx.network.lan.Jill
import com.dailystudio.devbricksx.network.lan.JillQuestion
import com.dailystudio.devbricksx.network.lan.JillAnswer
import com.dailystudio.devbricksx.network.lan.JillEntity
import com.dailystudio.devbricksx.samples.common.RandomNames
import com.dailystudio.devbricksx.samples.jackandjill.NearByJill
import com.dailystudio.devbricksx.samples.jackandjill.NearByJillManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NearByJillViewModelExt(application: Application): NearByJillViewModel(application) {

    companion object {
        const val TOPIC_NAME = "name"
        const val TOPIC_READY = "ready"
        const val TOPIC_PLAY = "play"

        const val KEY_READY = "ready"
        const val KEY_SEQ = "seq"

        val myJillName = RandomNames.nextName()
        val myJillId: String = System.currentTimeMillis().toString()
    }

    val type: String = JackAndJill.DEFAULT_TYPE

    private val _jillQuestion: MutableLiveData<JillQuestion> = MutableLiveData()
    val jillQuestion: LiveData<JillQuestion> = _jillQuestion

    private var _ready: MutableLiveData<Boolean> = MutableLiveData(false)
    val ready: LiveData<Boolean> =_ready

    private val myJill: Jill = object : Jill(type = type, myJillId) {

        override fun answerQuestion(question: JillQuestion): JillAnswer {
            _jillQuestion.postValue(question)

            return when (question.topic) {
                TOPIC_NAME -> {
                    JillAnswer(message = myJillName)
                }

                TOPIC_READY -> {
                    val ready = try {
                        question.extras[KEY_READY].toBoolean()
                    } catch (e: Exception) {
                        Logger.error("failed to parse ready from [$question]: $e")
                        null
                    } ?: return JillAnswer()

                    getNearByJill(question.from)?.also {
                        it.ready = ready
                        updateNearByJill(it)
                    }

                    JillAnswer()
                }

                else -> {
                    JillAnswer(message = "${question.topic}, got it.")
                }
            }
        }

    }

    private val myJack: Jack = Jack(type = type,
        myJillId = myJillId,
        ignores = listOf(myJillId),
        scope = viewModelScope)

    private val jillsObserver = Observer<List<JillEntity>> {
        Logger.debug("new jills arrived: $it")

        NearByJillManager.clear()

        it.forEach { jillInfo ->
            val nearByJill = NearByJill(
                id = jillInfo.jillId
            ).apply {
                name = buildString {
                    append(jillInfo.serviceIp)
                    append(" [")
                    append(jillInfo.servicePort)
                    append("]")
                }
            }

            addJill(nearByJill)
        }
    }


    init {
        myJack.jills.observeForever(jillsObserver)

        myJack.discover(application)
        myJill.online(application)
    }

    override fun onCleared() {
        myJack.jills.removeObserver(jillsObserver)

        myJill.offline()
        myJack.stopDiscover()
    }

    fun addJill(jill: NearByJill) {
        insertNearByJill(jill)
        askName(jill.id)
    }

    fun askName(jillId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val ret = myJack.askQuestion(jillId, TOPIC_NAME)
            if (ret.code == JillAnswer.STATUS_OK) {
                getNearByJill(jillId)?.also {
                    it.name = ret.message
                    updateNearByJill(it)
                }
            }
        }
    }

    fun toggleReady() {
        val valOfNewReady = !(_ready.value ?: false)

        viewModelScope.launch(Dispatchers.IO) {
            _ready.postValue(valOfNewReady)
            val jills = myJack.jills.value ?: return@launch
            for (j in jills) {
                myJack.askQuestion(
                    j.jillId,
                    TOPIC_READY,
                    mapOf(KEY_READY to valOfNewReady.toString())
                )
            }
        }
    }

    fun startPlay() {
        viewModelScope.launch(Dispatchers.IO) {
            val jills = myJack.jills.value ?: return@launch

            var seqIndex = 1
            for (j in jills) {
                val nearByJill = getNearByJill(j.jillId) ?: continue

                if (nearByJill.ready) {

                    myJack.askQuestion(
                        j.jillId,
                        TOPIC_PLAY,
                        mapOf(KEY_SEQ to seqIndex.toString())
                    )

                    seqIndex++
                }
            }
        }
    }

}