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

data class JillEntity (
    val jillId: String,
    val serviceName: String,
    val servicePort: Int,
    val serviceIp: String
) {
    lateinit var jillQAClient: JillQAClient
}

open class Jack(
    val myJillId: String,
    val type: String = JackAndJill.DEFAULT_TYPE,
    val ignores: List<String> = emptyList(),
    scope: CoroutineScope? = null
) {

    companion object {
        private const val DISCOVER_SERVICE_RETRY = 5
        private const val DISCOVER_RETRY_INTERVAL_BASE = 500L
        private const val DISCOVER_RETRY_INTERVAL_INC = 100L

        fun jillIdFromNsdServiceName(serviceName: String): String {
            return serviceName.replaceFirst(
                JackAndJill.SERVICE_BASE_NAME, "")
        }

        fun jillIdToNsdServiceName(jillId: String): String {
            return buildString {
                append(JackAndJill.SERVICE_BASE_NAME)
                append(jillId)
            }
        }
    }

    private val executor = Executors.newFixedThreadPool(4)
    private var nsdManager: NsdManager? = null

    private val jackScope: CoroutineScope

    private val _jillEntities: MutableLiveData<List<JillEntity>> =
        MutableLiveData(emptyList())

    val jills: LiveData<List<JillEntity>> = _jillEntities

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

    open suspend fun askQuestion(jillId: String,
                                 topic: String,
                                 extras: Map<String, String> = mapOf()): JillAnswer {
        return withContext(Dispatchers.IO) {
            val jill = findJill(jillId)
            Logger.debug("found jill = $jill [jillId: $jillId]")
            jill?.jillQAClient?.askQuestion(myJillId, topic, extras) ?: JillAnswer.ERROR
        }
    }

    private suspend fun toJillEntity(serviceInfo: NsdServiceInfo): JillEntity? {
        val serviceName = serviceInfo.serviceName ?: return null
        val servicePort = serviceInfo.port
        val serviceIp = if (Build.VERSION.SDK_INT >= 34) {
            serviceInfo.hostAddresses[0].hostAddress
        } else {
            serviceInfo.host.hostAddress ?: ""
        }

        Logger.debug("check-in jill: $serviceName, [ip: $serviceIp, port: $servicePort]")

        val newJill = JillEntity(
            jillIdFromNsdServiceName(serviceName),
            serviceName,
            servicePort,
            serviceIp)

        val jillQAClient = JillQAClient(myJillId)

        jillQAClient.connect(newJill)

        newJill.jillQAClient = jillQAClient

        return newJill
    }

    private fun findJill(jillId: String): JillEntity? {
        return _jillEntities.value?.firstOrNull {
            it.jillId == jillId
        }
    }

    private fun checkInEntity(jillEntity: JillEntity) {
        Logger.debug("check-in Jill: $jillEntity")

        val list = mutableListOf<JillEntity>()
        _jillEntities.value?.forEach {
            list.add(it)
        }

        list.add(jillEntity)

        _jillEntities.postValue(list)
    }

    private fun checkOutEntity(serviceInfo: NsdServiceInfo) {
        Logger.debug("check-out Jill: $serviceInfo")
        val serviceName = serviceInfo.serviceName

        var jillEntity: JillEntity? = null

        val list = mutableListOf<JillEntity>()
        _jillEntities.value?.forEach {
            if (it.serviceName == serviceName) {
                jillEntity = it
            } else {
                list.add(it)
            }
        }

        jackScope.launch(Dispatchers.IO) {
            jillEntity?.jillQAClient?.disconnect()
        }

        _jillEntities.postValue(list)
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
                val serviceId = jillIdFromNsdServiceName(serviceName)
                if (ignores.contains(serviceId)) {
                    Logger.debug("ignore service id: $serviceId")
                } else {
                    jackScope.launch(Dispatchers.IO) {
                        var nsdInfo: NsdServiceInfo? = null
                        for (i in 0 until 3) {
                            val delay = DISCOVER_RETRY_INTERVAL_BASE + i * DISCOVER_RETRY_INTERVAL_INC
                            Logger.debug("trying to resolve info: ${i + 1} time(s), delay = $delay")
                            delay(delay)
                            nsdInfo = resolveNsdServiceInfo(service)
                            if (nsdInfo != null) {
                                break
                            }
                        }

                        nsdInfo?.also {
                            toJillEntity(it)?.also { jillInfo ->
                                checkInEntity(jillInfo)
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
                    checkOutEntity(it)
                }
            }
        }

    }

}
