package com.dailystudio.devbricksx.network.lan

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.ResolveListener
import android.net.nsd.NsdServiceInfo
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dailystudio.devbricksx.development.Logger
import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.Executors

object JackAndJill {

    const val DEFAULT_TYPE = "_http._tcp"
    const val SERVICE_BASE_NAME = "JackAndJill"

    const val ATTR_ONLINE_TIME = "online-time"

    const val PORT = 59420
    const val COUPLE_PORT = 1314
}

class Jack(val type: String = JackAndJill.DEFAULT_TYPE) {

    private var nsdManager: NsdManager? = null
    private val executor = Executors.newSingleThreadExecutor()

    private var onlineTime: Long = 0

    private val _jills: MutableLiveData<List<String>> = MutableLiveData(emptyList())

    val jills: LiveData<List<String>> = _jills

    fun discover(context: Context, time: Long) {
        Logger.debug("Jack starts discovering Jills ... [time: $time]")
        onlineTime = time

        nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        if (nsdManager == null) {
            Logger.warn("Network Service Discovery not supported!")
        } else {
            nsdManager?.discoverServices(
                type,
                NsdManager.PROTOCOL_DNS_SD,
                discoverListener
            )
        }
    }

    fun stopDiscover() {
        Logger.debug("Jack stops discovering Jills ...")
        nsdManager?.stopServiceDiscovery(discoverListener)
    }

    private fun checkInServiceInfo(serviceInfo: NsdServiceInfo) {
        val serviceName = serviceInfo.serviceName ?: return

        val serviceOnlineTime = serviceName.replaceFirst(
            JackAndJill.SERVICE_BASE_NAME, "")
            .toLong()

        Logger.debug("check-in jill: $serviceName, [online: $serviceOnlineTime]")

        if (serviceOnlineTime == onlineTime) {
            Logger.debug("skip myself")
        } else {
            val list = mutableListOf<String>()
            _jills.value?.forEach {
                list.add(it)
            }
            list.add(serviceName)

            _jills.postValue(list)
        }
    }

    private fun checkOffServiceInfo(serviceInfo: NsdServiceInfo) {
        if (serviceInfo.serviceType == type) {
            Logger.debug("check-out Jill: $serviceInfo")
            val serviceName = serviceInfo.serviceName

            _jills.postValue(_jills.value?.filter {
                it != serviceName
            })
        }
    }

    private fun checkAndResolveService(serviceInfo: NsdServiceInfo?) {
        val info = serviceInfo?: return
        Logger.debug("new NSD service found: service = $info")


        if (Build.VERSION.SDK_INT >= 34) {
            nsdManager?.registerServiceInfoCallback(
                info,
                executor,
                object : NsdManager.ServiceInfoCallback {
                    override fun onServiceInfoCallbackRegistrationFailed(p0: Int) {
                        Logger.error("callback registration failed: p0 = $p0")
                    }

                    override fun onServiceUpdated(p0: NsdServiceInfo) {
                        Logger.debug("service update: $p0")
                        checkInServiceInfo(p0)
                    }

                    override fun onServiceLost() {
                        Logger.error("service lost")
                    }

                    override fun onServiceInfoCallbackUnregistered() {
                        Logger.error("callback unregistered")
                    }

                }
            )
        } else {
            nsdManager?.resolveService(
                info,
                object : ResolveListener {
                    override fun onResolveFailed(p0: NsdServiceInfo?, errorCode: Int) {
                        Logger.debug("resolve jill info failed: $errorCode")
                    }

                    override fun onServiceResolved(p0: NsdServiceInfo?) {
                        p0?.let {
                            checkInServiceInfo(it)
                        }
                    }
                }
            )
        }
    }

    private val discoverListener = object: NsdManager.DiscoveryListener {

        override fun onStartDiscoveryFailed(p0: String?, p1: Int) {}

        override fun onStopDiscoveryFailed(p0: String?, p1: Int) {}

        override fun onDiscoveryStarted(regType: String?) {
            Logger.debug("Jill discovery started: regType = $regType", regType)
        }

        override fun onDiscoveryStopped(serviceType: String?) {
            Logger.debug("Jill discovery stopped: serviceType = $serviceType")
        }

        override fun onServiceFound(service: NsdServiceInfo?) {
            if (service?.serviceType != type) {
                Logger.warn("ignore unmatched service: ${service?.serviceType} [required: ${type}]")
            } else {
                checkAndResolveService(service)
            }
        }

        override fun onServiceLost(service: NsdServiceInfo?) {
            service?.let {
                checkOffServiceInfo(it)
            }
        }

    }

}

class Jill(val type: String = JackAndJill.DEFAULT_TYPE) {

    private var nsdManager: NsdManager? = null

    var onlineTime: Long = 0
    var servicePort: Int = -1

    init {
        if (nsdManager == null) {
            Logger.warn("Network Service Discovery not supported!")
        }
    }

    @WorkerThread
    fun online(context: Context) {
        Logger.debug("Jill is going on line ...")
        nsdManager = context.getSystemService(Context.NSD_SERVICE) as? NsdManager
        onlineTime = System.currentTimeMillis()
        servicePort = allocatePort()

        val serviceName = buildString {
            append(JackAndJill.SERVICE_BASE_NAME)
            append(onlineTime)
        }

        val serviceInfo = NsdServiceInfo().apply {
            this.serviceName = serviceName

            serviceType = type
            port = servicePort
        }

        Logger.debug("Jill is going on line ... [name: $serviceName, port: $servicePort]")

        nsdManager?.registerService(
            serviceInfo, NsdManager.PROTOCOL_DNS_SD,
            registrationListener
        )
    }

    fun offline() {
        nsdManager?.unregisterService(registrationListener)
    }

    private fun allocatePort(): Int {
        var socket: ServerSocket? = null
        socket = try {
            ServerSocket(0)
        } catch (e: IOException) {
            Logger.error("could not create server socket: %s", e.toString())
            null
        }
        return socket?.localPort ?: -1
    }

    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onRegistrationFailed(service: NsdServiceInfo?, errorCode: Int) {
            Logger.error("Jill [${service?.serviceName}] online failed: err(${errorCode})", )
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