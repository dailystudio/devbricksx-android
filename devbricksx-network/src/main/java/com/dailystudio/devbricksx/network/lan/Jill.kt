package com.dailystudio.devbricksx.network.lan

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.annotation.WorkerThread
import com.dailystudio.devbricksx.development.Logger
import com.google.gson.Gson
import java.io.IOException
import java.net.ServerSocket

open class JillQuestion(
    val from: String,
    val topic: String,
    val extras: Map<String, String> = mapOf()
) {
}

open class JillAnswer(
    val code: Int = STATUS_OK,
    val message: String = "",
    val extras: Map<String, String> = mapOf()
) {
    companion object {
        const val STATUS_OK = 0
        const val STATUS_ERROR = -1
        const val STATUS_ERROR_NOT_FOUND = 404

        private const val KEY_ERROR = "error"
        private const val KEY_MESSAGE = "message"

        val ERROR = JillAnswer(
            STATUS_ERROR,
            "unknown error"
        )

        val ERROR_NOT_FOUND = JillAnswer(
            STATUS_ERROR_NOT_FOUND,
            "not found"
        )
    }
}

abstract class Jill(
    val type: String = JackAndJill.DEFAULT_TYPE,
    val id: String,
) {

    companion object {
        val GSON = Gson()
    }

    private var nsdManager: NsdManager? = null
    private var jillQADaemon: JillQADaemon? = null

    var servicePort: Int = -1

    private val cmdHandler = object : JillQAHandler {

        override fun answerQuestion(msgOfQuestion: String): String {

            val cmd = try {
                GSON.fromJson(msgOfQuestion, JillQuestion::class.java)
            } catch (e: Exception) {
                Logger.error("failed to parse jill cmd from [$msgOfQuestion]: $e")
                null
            }

            val ret = cmd?.let {
                this@Jill.answerQuestion(it)
            } ?: JillAnswer.ERROR

            return GSON.toJson(ret)
        }

    }

    @WorkerThread
    fun online(context: Context) {
        Logger.debug("Jill is going on line ...")
        nsdManager = context.getSystemService(Context.NSD_SERVICE) as? NsdManager
        servicePort = allocatePort()

        val serviceName = buildString {
            append(JackAndJill.SERVICE_BASE_NAME)
            append(id)
        }

        val serviceInfo = NsdServiceInfo().apply {
            this.serviceName = serviceName

            serviceType = JackAndJill.toNsdType(type)
            port = servicePort
        }

        Logger.debug("Jill is going on line ... [name: $serviceName, port: $servicePort]")

        jillQADaemon = JillQADaemon(cmdHandler).apply {
            start(servicePort)
        }

        nsdManager?.registerService(
            serviceInfo, NsdManager.PROTOCOL_DNS_SD,
            registrationListener
        )
    }

    fun offline() {
        Logger.debug("Jill is going offline ... [port: $servicePort]")

        nsdManager?.unregisterService(registrationListener)
        jillQADaemon?.stop()
    }

    abstract fun answerQuestion(question: JillQuestion): JillAnswer

    private fun allocatePort(): Int {
        var socket: ServerSocket? = null
        socket = try {
            ServerSocket(0)
        } catch (e: IOException) {
            Logger.error("could not create server socket: %s", e.toString())
            null
        }

        val port = socket?.localPort ?: -1

        socket?.close()

        return port
    }

    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onRegistrationFailed(service: NsdServiceInfo?, errorCode: Int) {
            Logger.error("Jill [${service?.serviceName}] online failed: err(${errorCode})")
        }

        override fun onUnregistrationFailed(service: NsdServiceInfo?, errorCode: Int) {
            Logger.error("Jill [${service?.serviceName}] offline failed: err(%d)", errorCode)
        }

        override fun onServiceRegistered(service: NsdServiceInfo?) {
            Logger.info("Jill [${service?.serviceName}]\'s online: port = ${service?.port}")
        }

        override fun onServiceUnregistered(service: NsdServiceInfo?) {
            Logger.info("Jill [${service?.serviceName}]\'s offline")
        }

    }
}