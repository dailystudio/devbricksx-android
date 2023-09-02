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
import com.dailystudio.devbricksx.samples.jackandjill.MyJill
import com.dailystudio.devbricksx.samples.jackandjill.MyJillManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyJillViewModelExt(application: Application): MyJillViewModel(application) {

    companion object {
        const val TOPIC_NAME = "name"
        const val TOPIC_READY = "ready"
        const val KEY_READY = "ready"

        val myJillName = RandomNames.nextName()
        val myJillId: String = System.currentTimeMillis().toString()
    }

    val type: String = JackAndJill.DEFAULT_TYPE

    private val _jillQuestion: MutableLiveData<JillQuestion> = MutableLiveData()
    val jillQuestion: LiveData<JillQuestion> = _jillQuestion

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

                    getMyJill(question.from)?.also {
                        it.ready = ready
                        updateMyJill(it)
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

        MyJillManager.clear()

        it.forEach { jillInfo ->
            val myJill = MyJill(
                id = jillInfo.jillId
            ).apply {
                name = buildString {
                    append(jillInfo.serviceIp)
                    append(" [")
                    append(jillInfo.servicePort)
                    append("]")
                }
            }

            addJill(myJill)
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

    fun addJill(jill: MyJill) {
        insertMyJill(jill)
        askName(jill.id)
    }

    fun askName(jillId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val ret = myJack.askQuestion(jillId, TOPIC_NAME)
            if (ret.code == JillAnswer.STATUS_OK) {
                getMyJill(jillId)?.also {
                    it.name = ret.message
                    updateMyJill(it)
                }
            }
        }
    }

    fun confirmReady(jillId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            myJack.askQuestion(jillId,
                TOPIC_READY, mapOf(KEY_READY to true.toString())
            )
        }
    }

}