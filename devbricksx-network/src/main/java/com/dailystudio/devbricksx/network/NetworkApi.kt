package com.dailystudio.devbricksx.network

import com.dailystudio.devbricksx.development.Logger
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okio.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.min


private fun ResponseBody.copy(limit: Long = Long.MAX_VALUE): ResponseBody {
    val source = source()
    source.request(limit)

    val bufferedCopy = source.buffer.clone()
    return ResponseBody.create(
        contentType(),
        contentLength(), bufferedCopy)
}

internal interface ProgressListener {
    fun update(
        identifier: String,
        buffer: Buffer,
        bytesRead: Long,
        contentLength: Long,
        done: Boolean
    )
}

private class ProgressResponseBody(val identifier: String,
                                   private val responseBody: ResponseBody,
                                   private val progressListener: ProgressListener
) : ResponseBody() {

    private var bufferedSource: BufferedSource? = null

    override fun contentType(): MediaType? {
        return responseBody.contentType()
    }

    override fun contentLength(): Long {
        return responseBody.contentLength()
    }

    override fun source(): BufferedSource {
        if (bufferedSource == null) {
            bufferedSource = source(responseBody.source()).buffer()
        }

        return bufferedSource!!
    }

    private fun source(source: Source): Source {
        return object : ForwardingSource(source) {
            var totalBytesRead = 0L

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead: Long = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                progressListener.update(
                    identifier,
                    sink,
                    totalBytesRead,
                    responseBody.contentLength(),
                    bytesRead == -1L
                )

                return bytesRead
            }
        }
    }


}

open class ApiReturn (var code: Int? = null,
                      var message: String? = null) {

    override fun toString(): String {
        return buildString {
            append(this::class.java.simpleName)
            append("(${hashCode()}): ")
            append("code: $code, ")
            append("code: $message, ")
        }
    }

}

data class ResponseProgress(val identifier: String,
                            val buffer: Buffer,
                            val bytesRead: Long,
                            val contentLength: Long,
                            val done: Boolean) {

    override fun toString(): String {
        return buildString {
            append("[$identifier]: ")
            append("progress = [$bytesRead/$contentLength], done = $done")
        }
    }

}

typealias ApiProgressCallback =
    (rp: ResponseProgress) -> Unit

abstract class HeaderInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
            .method(original.method, original.body)

        val headers = getHeaders()

        headers?.entries?.forEach{
            builder.addHeader(it.key, it.value)
        }

        val request = builder.build()

        return chain.proceed(request)
    }

    abstract fun getHeaders(): Map<String, String>?

}

abstract class NetworkApi<Interface> {

    companion object {

        const val HEADER_PROGRESS_IDENTIFIER = "progress-identifier"

        const val DEFAULT_TIMEOUT: Long = 10000

    }

    protected enum class ResponseType {
        JSON,
        Raw,
    }

    protected class ApiOptions(
        val respType: ResponseType = ResponseType.JSON,
        var debugOutputBufferLen: Int = 1204,

        var connectionTimeout: Long? = DEFAULT_TIMEOUT,
        var readTimeout: Long? = DEFAULT_TIMEOUT,
        var writeTimeout: Long? = DEFAULT_TIMEOUT,

        var interceptors: List<Interceptor>? = null,
        var networkInterceptors: List<Interceptor>? = null,
    )

    internal class DebugRequestInterceptor(private val bufferLen: Int = 1024): Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            Logger.debug("request [headers]: ${request.headers}", )
            Logger.debug("request [url]: ${request.url}", )
            val buffer = Buffer()
            request.body?.writeTo(buffer)

            val bufferInUtf8 = buffer.readUtf8()
            val maxLength =
                min(bufferInUtf8.length, bufferLen)
            if (maxLength > 0) {
                Logger.debug("request [body]: ${bufferInUtf8.substring(0, maxLength)}",)
            }

            return chain.proceed(request)
        }

    }

    internal class DebugResponseInterceptor(private val bufferLen: Int = 1024): Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response = chain.proceed(request)

            val responseBody = response.body
            val bodyCopied = responseBody?.copy(bufferLen.toLong())

            val responseBodyString = try {
                bodyCopied?.string()
            } catch (e: Exception) {
                Logger.debug("failed to convert body to string: $e")
                null
            }

            Logger.debug("response [body]: $responseBodyString")

            return response.newBuilder().body(
                responseBody
            ).build()
        }
    }

    internal class ProgressInterceptor(
        private val listener: ProgressListener): Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val originalResponse = chain.proceed(request)

            val identifier = request.header(
                HEADER_PROGRESS_IDENTIFIER
            )

            return if (identifier != null && identifier.isNotBlank()) {
                originalResponse.newBuilder()
                    .body(
                        ProgressResponseBody(
                            identifier,
                            originalResponse.body!!,
                            listener)
                    )
                    .build()
            } else  {
                val responseBody = originalResponse.body

                return originalResponse.newBuilder().body(
                    ResponseBody.create(
                        responseBody!!.contentType(),
                        responseBody.bytes()
                    )
                ).build()
            }
        }
    }

    protected open fun createInterface(
        options: ApiOptions,
    ): Interface {
        val client = buildOkhttpClient(options)

        val builder = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)

        if (options.respType == ResponseType.JSON) {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            builder.addConverterFactory(GsonConverterFactory.create(gson))
        }

        return builder.build().create(classOfInterface)
    }

    protected open fun buildOkhttpClient(options: ApiOptions): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            .hostnameVerifier { _, _ -> true }
            .addNetworkInterceptor(ProgressInterceptor(progressListener))

        if (debugEnabled) {
            clientBuilder.addNetworkInterceptor(
                DebugRequestInterceptor(options.debugOutputBufferLen)
            )

            clientBuilder.addInterceptor(DebugResponseInterceptor(options.debugOutputBufferLen))
        }

        options.connectionTimeout?.let {
            clientBuilder.connectTimeout(it, TimeUnit.MILLISECONDS)
        }

        options.readTimeout?.let {
            clientBuilder.readTimeout(it, TimeUnit.MILLISECONDS)
        }

        options.writeTimeout?.let {
            clientBuilder.writeTimeout(it, TimeUnit.MILLISECONDS)
        }

        options.interceptors?.forEach {
            clientBuilder.addInterceptor(it)
        }

        options.networkInterceptors?.forEach {
            clientBuilder.addNetworkInterceptor(it)
        }

        return clientBuilder.build()
    }

    protected open fun getInterface(type: ResponseType = ResponseType.JSON): Interface {
        return mapOfInterfaces[type]
            ?: createInterface(getApiOptions(type)).also {
                mapOfInterfaces[type] = it
            }
    }

    protected open fun resetInterface(type: ResponseType? = ResponseType.JSON) {
        mapOfInterfaces.remove(type)
    }

    protected fun resetInterfaces() {
        mapOfInterfaces.clear()
    }

    protected open fun getApiOptions(type: ResponseType): ApiOptions {
        return ApiOptions(type)
    }

    fun debugApi(format: String, vararg args: Any?) {
        if (debugEnabled) {
            val builder = StringBuilder("[RESTFul API] ")

            builder.append(format)

            Logger.debug(builder.toString(), *args)
        }
    }

    fun warnApi(format: String, vararg args: Any?) {
        val builder = StringBuilder("[RESTFul API] ")

        builder.append(format)

        Logger.warn(builder.toString(), *args)
    }

    fun errorApi(format: String, vararg args: Any?) {
        val builder = StringBuilder("[RESTFul API] ")

        builder.append(format)

        Logger.error(builder.toString(), *args)
    }

    private val mapOfInterfaces: MutableMap<ResponseType, Interface> =
        mutableMapOf()

    protected open val debugEnabled: Boolean = true

    protected abstract val baseUrl: String
    protected abstract val classOfInterface: Class<Interface>

    private val progressCallbacks =
        mutableListOf<ApiProgressCallback>()

    private val progressListener = object : ProgressListener {

        override fun update(
            identifier: String,
            buffer: Buffer,
            bytesRead: Long,
            contentLength: Long,
            done: Boolean) {
            val rp = ResponseProgress(identifier,
                buffer, bytesRead, contentLength, done)

//            Logger.debug("notify: rp = $rp")

            progressCallbacks.forEach {
                it.invoke(rp)
            }
        }

    }

    suspend fun <T> catchApiCallOrNull(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T,
    ): T? {
        return withContext(dispatcher) {
            try {
                apiCall.invoke()
            } catch (throwable: Throwable) {
                Logger.error("failed to call api [${apiCall.javaClass.enclosingMethod?.name}]: $throwable")
                null
            }
        }
    }

    suspend fun <T> progressApiCall(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        apiCall: suspend () -> T,
        callback: ApiProgressCallback? = null
    ): T? {
        return withContext(dispatcher) {
            if (callback != null) {
                progressCallbacks.add(callback)
            }

            try {
                apiCall.invoke()
            } catch (throwable: Throwable) {
                Logger.error("failed to call api [${apiCall.javaClass.enclosingMethod?.name}]: $throwable")
                null
            } finally {
                if (callback != null) {
                    progressCallbacks.remove(callback)
                }
            }
        }
    }
}