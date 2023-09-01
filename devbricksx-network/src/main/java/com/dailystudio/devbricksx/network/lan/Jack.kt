package com.dailystudio.devbricksx.network.lan

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


data class JillInfo (
    val serviceName: String,
    val servicePort: Int,
    val serviceIp: String,
) {
    var jillCmdC: JillCmdC = JillCmdC()
}

open class Jack(
    val type: String = JackAndJill.DEFAULT_TYPE,
    val ignores: List<String> = emptyList(),
    scope: CoroutineScope? = null
) {

    private val executor = Executors.newFixedThreadPool(4)
    private var nsdManager: NsdManager? = null

    private val jackScope: CoroutineScope

    private val _jills: MutableLiveData<List<JillInfo>> = MutableLiveData(emptyList())

    val jills: LiveData<List<JillInfo>> = _jills

    init {
        jackScope = if (scope != null) {
            CoroutineScope(scope.coroutineContext + executor.asCoroutineDispatcher())
        } else {
            CoroutineScope(executor.asCoroutineDispatcher())
        }
    }

    fun discover(context: Context) {
        Logger.debug("Jack starts discovering Jills ...")

        nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        if (nsdManager == null) {
            Logger.warn("Network Service Discovery not supported!")
        } else {
            nsdManager?.discoverServices(
                JackAndJill.toNsdType(type),
                NsdManager.PROTOCOL_DNS_SD,
                discoverListener
            )
        }
    }

    fun stopDiscover() {
        Logger.debug("Jack stops discovering Jills ...")
        nsdManager?.stopServiceDiscovery(discoverListener)
    }

    open suspend fun askJill(jillId: String, message: String): String? {
        return withContext(Dispatchers.IO) {
            val jill = findJill(jillId)
            Logger.debug("found jill = $jill")
            jill?.jillCmdC?.ask(message)
        }
    }

    private suspend fun wrapJillInfo(serviceInfo: NsdServiceInfo): JillInfo? {
        val serviceName = serviceInfo.serviceName ?: return null
        val servicePort = serviceInfo.port
        val serviceIp = if (Build.VERSION.SDK_INT >= 34) {
            serviceInfo.hostAddresses[0].hostAddress
        } else {
            serviceInfo.host.hostAddress ?: ""
        }

        Logger.debug("check-in jill: $serviceName, [ip: $serviceIp, port: $servicePort]")

        val newJill = JillInfo(serviceName, servicePort, serviceIp)

        val jillCmdC = JillCmdC()

        jillCmdC.connect(newJill)
        jillCmdC.ask("Hi Jill")

        newJill.jillCmdC = jillCmdC

        return newJill
    }

    private fun findJill(jillId: String): JillInfo? {
        return _jills.value?.firstOrNull {
            it.serviceName == jillId
        }
    }

    private fun checkInJillInfo(jillInfo: JillInfo) {
        Logger.debug("check-in Jill: $jillInfo")

        val list = mutableListOf<JillInfo>()
        _jills.value?.forEach {
            list.add(it)
        }

        list.add(jillInfo)

        _jills.postValue(list)
    }

    private fun checkOutJillInfo(serviceInfo: NsdServiceInfo) {
        Logger.debug("check-out Jill: $serviceInfo")
        val serviceName = serviceInfo.serviceName

        var jillInfo: JillInfo? = null

        val list = mutableListOf<JillInfo>()
        _jills.value?.forEach {
            if (it.serviceName == serviceName) {
                jillInfo = it
            } else {
                list.add(it)
            }
        }

        jackScope.launch(Dispatchers.IO) {
            jillInfo?.jillCmdC?.disconnect()
        }

        _jills.postValue(list)
    }

    private suspend fun resolveNsdServiceInfo(serviceInfo: NsdServiceInfo) = suspendCoroutine<NsdServiceInfo?> { continuation ->
        Logger.debug("new NSD service found: service = $serviceInfo")

        if (Build.VERSION.SDK_INT >= 34) {
            nsdManager?.registerServiceInfoCallback(
                serviceInfo,
                executor,
                object : NsdManager.ServiceInfoCallback {
                    override fun onServiceInfoCallbackRegistrationFailed(p0: Int) {
                        Logger.error("callback registration failed: p0 = $p0")
                        continuation.resume(null)
                    }

                    override fun onServiceUpdated(p0: NsdServiceInfo) {
                        Logger.debug("service update: $p0")
                        continuation.resume(p0)
                    }

                    override fun onServiceLost() {
                        Logger.error("service lost")
                        continuation.resume(null)
                    }

                    override fun onServiceInfoCallbackUnregistered() {
                        Logger.error("callback unregistered")
                        continuation.resume(null)
                    }

                }
            )
        } else {
            nsdManager?.resolveService(
                serviceInfo,
                object : NsdManager.ResolveListener {
                    override fun onResolveFailed(p0: NsdServiceInfo?, errorCode: Int) {
                        Logger.debug("resolve jill info failed: $errorCode")
                        continuation.resume(null)
                    }

                    override fun onServiceResolved(p0: NsdServiceInfo?) {
                        p0?.let {
                            continuation.resume(p0)
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
            Logger.debug("Nsd service found: ${service?.serviceName}")
            if (service?.serviceType != JackAndJill.toNsdType(type)) {
                Logger.warn("ignore unmatched service: ${service?.serviceType} [required: ${type}]")
            } else {
                val serviceName = service.serviceName
                val serviceId = serviceName.replaceFirst(
                    JackAndJill.SERVICE_BASE_NAME, "")
                if (ignores.contains(serviceId)) {
                    Logger.debug("ignore service id: $serviceId")
                } else {
                    jackScope.launch(Dispatchers.IO) {
                        var nsdInfo: NsdServiceInfo? = null
                        for (i in 0 until 3) {
                            delay(400)
                            Logger.debug("trying to resolve info: ${i + 1} time(s)")
                            nsdInfo = resolveNsdServiceInfo(service)
                            if (nsdInfo != null) {
                                break
                            }
                        }

                        nsdInfo?.also { it ->
                            wrapJillInfo(it)?.also { jillInfo ->
                                checkInJillInfo(jillInfo)
                            }
                        }
                    }
                }
            }
        }

        override fun onServiceLost(service: NsdServiceInfo?) {
            Logger.debug("Nsd service lost: ${service?.serviceName}")
            service?.let {
                if (it.serviceType == JackAndJill.toNsdType(type)) {
                    checkOutJillInfo(it)
                }
            }
        }

    }

}
