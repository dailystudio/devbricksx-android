package com.dailystudio.devbricksx.network

abstract class AuthenticatedNetworkApi<Interface>: NetworkApi<Interface>() {

    private var authInfo: Map<String, String>? = null
    protected open val defaultAuthInfo: Map<String, String> = emptyMap()

    private var mHeaderInterceptor = object : HeaderInterceptor() {

        override fun getHeaders(): Map<String, String> {
            return getAuthInfo()
        }

    }

    override fun getApiOptions(type: ResponseType): ApiOptions {
        return ApiOptions(
            type,
            interceptors = listOf(mHeaderInterceptor)
        )
    }

    @Synchronized
    protected fun getAuthInfo(): Map<String, String> {
        return authInfo ?: defaultAuthInfo
    }

    @Synchronized
    open fun setAuthInfo(info: Map<String, String>) {
        authInfo = mutableMapOf<String, String>().apply {
            putAll(info)
        }

        resetInterfaces()
    }


}