package com.dailystudio.devbricksx.network.lan

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.annotation.WorkerThread
import com.dailystudio.devbricksx.development.Logger
import java.io.IOException
import java.net.ServerSocket


class Jill(
    val type: String = JackAndJill.DEFAULT_TYPE,
    val id: String,
    val handler: JillCmdHandler
) {
    private var nsdManager: NsdManager? = null
    private var jillCmdD: JillCmdD? = null

    var servicePort: Int = -1

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

        jillCmdD = JillCmdD(handler).apply {
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
        jillCmdD?.stop()
    }

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