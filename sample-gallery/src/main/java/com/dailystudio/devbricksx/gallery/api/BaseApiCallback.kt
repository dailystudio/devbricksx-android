package com.dailystudio.devbricksx.gallery.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class BaseApiCallback<T> : Callback<T> {

    private var response: T? = null

    override fun onResponse(call: Call<T>, response: Response<T>) {
        this.response = response.body()
        BaseApi.debugApi("api response: [%s]", this.response)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        BaseApi.warnApi("Api request failed: %s", t)
    }

}
