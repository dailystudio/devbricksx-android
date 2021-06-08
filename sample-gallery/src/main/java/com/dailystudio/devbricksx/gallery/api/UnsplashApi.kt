package com.dailystudio.devbricksx.gallery.api

import android.content.Context
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.api.data.Links
import com.dailystudio.devbricksx.gallery.api.data.PagedPhotos
import com.dailystudio.devbricksx.gallery.api.data.Photo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

interface UnsplashApiInterface {

    companion object {
        const val BASE_URL = "https://api.unsplash.com"
        const val PHOTOS_PATH = "photos"

        const val PARAM_CLIENT_ID = "client_id"
        const val PARAM_PAGE = "page"
        const val PARAM_PER_PAGE = "per_page"
    }


    @GET("/$PHOTOS_PATH")
    fun listPhotos(
        @Query(PARAM_PAGE)page: Int = 1,
        @Query(PARAM_PER_PAGE)perPage: Int = 10
    ): Call<Array<Photo>>

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
                   page: Int = 1,
                   perPage: Int = 10,
                   callback: Callback<Array<Photo>>? = null
    ): PagedPhotos? {
        val uniAppInterface = getInterface()

        val call = uniAppInterface?.listPhotos(page, perPage)
        if (callback == null) {
            var ret: Array<Photo>? = null
            var links: Links? = null
            try {
                val response = call?.execute()
                if (response != null) {
                    ret = response.body()
                    links = Links.fromString(response.headers()["Link"])
                }
            } catch (e: IOException) {
                Logger.error(
                    "list photos failed: %s",
                    e.toString()
                )

                ret = null
                links = null
            }

            return PagedPhotos(ret, links)
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