package com.dailystudio.devbricksx.gallery.api

import android.content.Context
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.data.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import java.io.IOException

interface UnsplashApiInterface {

    companion object {
        const val BASE_URL = "https://api.unsplash.com"
        const val PHOTOS_PATH = "photos"

        const val PARAM_CLIENT_ID = "client_id"
    }


    @GET("/$PHOTOS_PATH")
    fun listPhotos(): Call<Array<Photo>>

}


class UnsplashApi: BaseApi<UnsplashApiInterface>() {

    companion object {
        private var sApiInterface: UnsplashApiInterface? = null

        @Synchronized
        fun resetApiInterface() {
            sApiInterface = null
        }

    }


    fun listPhotos(context: Context,
                   callback: Callback<Array<Photo>>? = null
    ): Array<Photo>? {
        val uniAppInterface = getInterface()

        val call = uniAppInterface?.listPhotos()
        if (callback == null) {
            var ret: Array<Photo>? = null
            try {
                val response = call?.execute()
                if (response != null) {
                    ret = response.body()
                }
            } catch (e: IOException) {
                Logger.error(
                    "list photos failed: %s",
                    e.toString()
                )

                ret = arrayOf()
            }

            return ret
        }

        call?.enqueue(callback)

        return null

    }

    @Synchronized
    private fun getInterface(): UnsplashApiInterface? {
        if (sApiInterface == null) {
            sApiInterface = createInterface(
                UnsplashApiInterface.BASE_URL,
                UnsplashApiInterface::class.java,
                false,
                listOf(mHeaderInterceptor)
            )
        }

        return sApiInterface
    }

    private var mHeaderInterceptor = object : HeaderInterceptor() {

        override fun getHeaders(): Map<String, String>? {
            return mutableMapOf(
                "Authorization" to "Client-ID nNy9rRzkq02t0Sp429fR4gG2vgiXbB9bR1AM2w03-y0"
            )
        }

    }

}