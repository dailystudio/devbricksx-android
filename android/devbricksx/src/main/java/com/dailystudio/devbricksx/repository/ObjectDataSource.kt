package com.dailystudio.devbricksx.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.dailystudio.devbricksx.development.Logger
import kotlin.math.min

class ObjectDataSource<Object>(private val objects: Map<*, Object>)
    : PageKeyedDataSource<Long, Object>() {

    override fun loadInitial(params: LoadInitialParams<Long>,
                             callback: LoadInitialCallback<Long, Object>) {
        val listOfObjects = objects.values.toList()

        val start = 0
        val end = min(listOfObjects.size, params.requestedLoadSize)
        Logger.debug("start: $start, end: $end")

        val next = if (end < listOfObjects.size) end else null

        callback.onResult(listOfObjects.subList(start, end), null, next?.toLong())
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Object>) {
        val listOfObjects = objects.values.toList()

        val start = params.key
        val end = min(listOfObjects.size.toLong(), start + params.requestedLoadSize)
        Logger.debug("start: $start, end: $end")

        val next = if (end < listOfObjects.size) end else null

        callback.onResult(listOfObjects.subList(start.toInt(), end.toInt()), next)
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Object>) {
    }

}

class ObjectDataSourceFactory<Object>(private val objects: Map<*, Object>)
    : DataSource.Factory<Long, Object>() {

    private val sourceLiveData = MutableLiveData<ObjectDataSource<Object>>()

    override fun create(): DataSource<Long, Object> {

        val source = ObjectDataSource(objects)

        sourceLiveData.postValue(source)

        return source
    }

    fun invalidateSource() {
        sourceLiveData.value?.invalidate()
    }

}