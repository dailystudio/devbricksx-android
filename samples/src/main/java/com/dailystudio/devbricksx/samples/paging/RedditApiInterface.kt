package com.dailystudio.devbricksx.samples.paging

import com.dailystudio.devbricksx.network.NetworkApi
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API communication setup
 */
interface RedditApiInterface {

    @GET("/r/{subreddit}/hot.json")
    suspend fun getTop(
            @Path("subreddit") subreddit: String,
            @Query("limit") limit: Int,
            @Query("after") after: String? = null,
            @Query("before") before: String? = null
    ): ListingResponse

    class ListingResponse(val data: ListingData)

    class ListingData(
            val children: List<RedditChildrenResponse>,
            val after: String?,
            val before: String?
    ) {
        override fun toString(): String {
            return buildString {
                append("[before: $before")
                append(", after: $after]")
                append(": children = $children")
            }
        }
    }

    data class RedditChildrenResponse(val data: RedditPost)

    companion object {
        const val BASE_URL = "https://www.reddit.com/"
    }
}

object RedditApi: NetworkApi<RedditApiInterface>() {

    suspend fun getTop(subreddit: String,
                       limit: Int,
                       after: String? = null,
                       before: String? = null): RedditApiInterface.ListingResponse {
        return getInterface().getTop(subreddit, limit, after, before)
    }

    override val debugEnabled: Boolean
        get() = true

    override val baseUrl: String
        get() = RedditApiInterface.BASE_URL
    override val classOfInterface: Class<RedditApiInterface>
        get() = RedditApiInterface::class.java

}
