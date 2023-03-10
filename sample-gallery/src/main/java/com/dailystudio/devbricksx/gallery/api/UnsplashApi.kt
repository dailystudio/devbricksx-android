package com.dailystudio.devbricksx.gallery.api

import android.net.Uri
import com.dailystudio.devbricksx.GlobalContextWrapper
import com.dailystudio.devbricksx.development.Logger
import com.dailystudio.devbricksx.gallery.R
import com.dailystudio.devbricksx.gallery.api.data.Links
import com.dailystudio.devbricksx.gallery.api.data.PageResults
import com.dailystudio.devbricksx.gallery.api.data.Photo
import com.dailystudio.devbricksx.gallery.api.data.User
import com.dailystudio.devbricksx.network.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.net.URLDecoder

interface ImageApiInterface {
    companion object {
        const val BASE_URL = "https://images.unsplash.com/"
    }

    @GET("/{downloadPath}")
    suspend fun download(
        @Path("downloadPath") path: String,
        @Header(NetworkApi.HEADER_PROGRESS_IDENTIFIER) id: Long
    ): ResponseBody
}

object ImageApi: NetworkApi<ImageApiInterface>() {

    override val baseUrl: String
        get() = ImageApiInterface.BASE_URL
    override val classOfInterface: Class<ImageApiInterface>
        get() = ImageApiInterface::class.java

    private fun extractDownloadPath(downloadUrl: String): String? {
        return Uri.parse(downloadUrl).lastPathSegment
    }

    suspend fun download(
        downloadUrl: String,
        downloadId: Long = System.currentTimeMillis(),
        callback: ApiProgressCallback? = null
    ): ByteArray? {
        return try {
            val downloadPath = extractDownloadPath(downloadUrl)
            Logger.debug("download path: $downloadPath")

            if (downloadPath.isNullOrEmpty()) {
                return null
            }

            val body = progressApiCall(
                apiCall = { getInterface(ResponseType.Raw).download(downloadPath, downloadId) },
                callback = callback)

            debugApi("content-length: ${body?.contentLength()}")
            debugApi("content-type: ${body?.contentType()}")

            val bytes = body?.bytes()
            debugApi("bytes: ${bytes?.size?: 0}B")

            bytes
        } catch (e: IOException) {
            Logger.error("read bytes from response failed: $e")

            null
        }
    }
}

interface UnsplashApiInterface {

    companion object {
        const val BASE_URL = "https://api.unsplash.com"
        const val PHOTOS_PATH = "photos"
        const val SEARCH_PATH = "search"
        const val USER_PATH = "user"

        const val PARAM_CLIENT_ID = "client_id"
        const val PARAM_PAGE = "page"
        const val PARAM_PER_PAGE = "per_page"
        const val PARAM_QUERY = "query"

        const val DEFAULT_PAGE = 1
        const val DEFAULT_PER_PAGE = 20
        const val MAX_PER_PAGE = 30

        fun getPageFromLink(link: String?): Int {
            if (link == null) {
                return 0
            }

            val uri = Uri.parse(link)

            val paramPage =
                uri.getQueryParameter(PARAM_PAGE) ?: DEFAULT_PAGE.toString()
            return try {
                paramPage.toInt()
            } catch (e: NumberFormatException) {
                DEFAULT_PAGE
            }
        }

        fun getPerPageFromLink(link: String?): Int {
            if (link == null) {
                return 0
            }

            val uri = Uri.parse(link)

            val paramPage =
                uri.getQueryParameter(PARAM_PER_PAGE) ?: DEFAULT_PER_PAGE.toString()

            return try {
                paramPage.toInt()
            } catch (e: NumberFormatException) {
                DEFAULT_PER_PAGE
            }
        }

    }

    @GET("$SEARCH_PATH/$PHOTOS_PATH")
    suspend fun searchPhotos(
        @Query(PARAM_QUERY)query: String,
        @Query(PARAM_PAGE)page: Int = 1,
        @Query(PARAM_PER_PAGE)perPage: Int = 10
    ): PageResults

    @GET("/$PHOTOS_PATH")
    fun listPhotos(
        @Query(PARAM_PAGE)page: Int = 1,
        @Query(PARAM_PER_PAGE)perPage: Int = 10
    ): Call<Array<Photo>>

    @GET("/$USER_PATH/{userName}")
    suspend fun getUser(
        @Path("userName") userName: String,
    ): User

}

object UnsplashApi: AuthenticatedNetworkApi<UnsplashApiInterface>() {

    suspend fun searchPhotos(query: String,
                     page: Int = 1,
                     perPage: Int = 10,
    ): PageResults? {
        val uniAppInterface = getInterface()

        return catchApiCallOrNull {
            uniAppInterface.searchPhotos(query, page, perPage)
        }
    }

    fun listPhotos(page: Int = 1,
                   perPage: Int = 10,
    ): PageResults {
        val uniAppInterface = getInterface()

        val call = uniAppInterface.listPhotos(page, perPage)
        var ret: Array<Photo>? = null
        var links: Links? = null
        try {
            val response = call.execute()

            ret = response.body()
            links = Links.fromString(response.headers()["Link"])
        } catch (e: IOException) {
            Logger.error(
                "list photos failed: %s",
                e.toString()
            )

            ret = null
            links = null
        }

        return PageResults(
            ret?.size ?: 0,
            UnsplashApiInterface.getPageFromLink(links?.last),
            ret,
            links)
    }

    suspend fun getUser(userName: String): User? {
        val uniAppInterface = getInterface()

        return catchApiCallOrNull {
            uniAppInterface.getUser(userName)
        }
    }

    override val defaultAuthInfo: Map<String, String>
        get() {
            val apiKey = GlobalContextWrapper.context?.let {
                it.getString(R.string.api_key)
            }

            return mutableMapOf(
                "Authorization" to "Client-ID $apiKey"
            )
        }

    override val baseUrl: String
        get() = UnsplashApiInterface.BASE_URL
    override val classOfInterface: Class<UnsplashApiInterface>
        get() = UnsplashApiInterface::class.java

}

