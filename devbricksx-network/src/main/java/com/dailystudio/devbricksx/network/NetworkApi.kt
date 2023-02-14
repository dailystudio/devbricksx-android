package com.dailystudio.devbricksx.network

import android.text.TextUtils
import com.dailystudio.devbricksx.development.Logger
import com.google.gson.GsonBuilder
import okhttp3.*
import okio.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.min

private const val DEBUG_API = true
private const val DEFAULT_TIMEOUT: Long = 10000

internal interface ProgressListener {
    fun update(
        identifier: String,
        bytesRead: Long,
        contentLength: Long,
        done: Boolean
    )
}

private class ProgressResponseBody(val identifier: String,
                                   private val responseBody: ResponseBody,
                                   progressListener: ProgressListener
) : ResponseBody() {

    private val progressListener: ProgressListener = progressListener
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
                    totalBytesRead,
                    responseBody.contentLength(),
                    bytesRead == -1L
                )

                return bytesRead
            }
        }
    }


}

open class BaseApiRet (var code: Int? = null,
                       var message: String? = null) {

    override fun toString(): String {
        return String.format("%s(0x%08x): %d(%s)",
            javaClass,
            hashCode(),
            code,
            message)
    }

}

data class ResponseProgress(val identifier: String,
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

abstract class HeaderInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
            .method(original.method(), original.body())

        val headers = getHeaders()

        headers?.entries?.forEach{
            builder.addHeader(it.key, it.value)
        }

        val request = builder.build()

        return chain.proceed(request)
    }

    abstract fun getHeaders(): Map<String, String>?

}

abstract class ProgressInterceptor: Interceptor {

    private val progressListener: ProgressListener =
        object : ProgressListener {
            override fun update(
                identifier: String,
                bytesRead: Long,
                contentLength: Long,
                done: Boolean) {
                val rp = ResponseProgress(identifier,
                    bytesRead, contentLength, done)

                Logger.debug("notify: rp = $rp")
                onResponseProgress(rp)
            }
        }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val originalResponse = chain.proceed(request)

        val identifier = request.header(
            NetworkApi.HEADER_PROGRESS_IDENTIFIER
        )

        return if (identifier != null && identifier.isNotBlank()) {
            originalResponse.newBuilder()
                .body(
                    ProgressResponseBody(
                        identifier,
                        originalResponse.body()!!,
                        progressListener)
                )
                .build()
        } else  {
            val responseBody = originalResponse.body()

            return originalResponse.newBuilder().body(
                ResponseBody.create(
                    responseBody!!.contentType(),
                    responseBody!!.bytes()
                )
            ).build()
        }
    }

    abstract fun onResponseProgress(rp: ResponseProgress)
}


open class NetworkApi<Interface> {

    internal class RawResponseDebugInterceptor: Interceptor {

            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request = chain.request()
                val response = chain.proceed(request)

                val responseBody = response.body()
                val responseBodyString = response.body()!!.string()
                Logger.debug("response [raw]: $responseBodyString")

                return response.newBuilder().body(
                    ResponseBody.create(
                        responseBody!!.contentType(),
                        responseBodyString.toByteArray()
                    )
                ).build()
            }
    }



    companion object {

        const val HEADER_PROGRESS_IDENTIFIER = "progress-identifier"
        const val EVENT_RESPONSE_PROGRESS = "event-response-progress"

        var networkInterceptorOutputMaxLength = 1024

        fun debugApi(format: String, vararg args: Any?) {
            if (DEBUG_API) {
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

    }

    private val mConnTimeout = DEFAULT_TIMEOUT

    protected fun createInterface(
        baseUrl: String,
        classOfInterface: Class<Interface>,
        rawResponse: Boolean = false,
        interceptors: List<Interceptor>? = null
    ): Interface? {
        if (TextUtils.isEmpty(baseUrl)) {
            return null
        }

        val clientBuilder = OkHttpClient.Builder()
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(mConnTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(mConnTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(mConnTimeout, TimeUnit.MILLISECONDS)
            .addNetworkInterceptor { chain ->
                val request = chain.request()
                debugApi("request [headers]: ${request.headers()}", )
                debugApi("request [url]: ${request.url()}", )
                val buffer = Buffer()
                request.body()?.writeTo(buffer)

                val bufferInUtf8 = buffer.readUtf8()
                val maxLength =
                    min(bufferInUtf8.length, networkInterceptorOutputMaxLength)
                debugApi("request [body]: ${bufferInUtf8.substring(0 ,maxLength)}", )

                chain.proceed(request)
            }
//            .addNetworkInterceptor(ProgressInterceptor())

        if (!rawResponse) {
            clientBuilder.addInterceptor(RawResponseDebugInterceptor())
        }

        if (interceptors != null && interceptors.isNotEmpty()) {
            interceptors.forEach {
                clientBuilder.addInterceptor(it)
            }
        }

        val client = clientBuilder.build()

        val builder = Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)

        if (!rawResponse) {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            builder.addConverterFactory(GsonConverterFactory.create(gson))
        }

        return builder.build().create(classOfInterface)
    }

    protected open fun createRawInterface(
        baseUrl: String,
        classOfInterface: Class<Interface>,
        interceptors: List<Interceptor>? = null
    ): Interface? {
        return createInterface(baseUrl, classOfInterface,
        true, interceptors)
    }


}