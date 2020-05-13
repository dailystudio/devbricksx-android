package com.dailystudio.devbricksx.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData

open class ObjectRepository<Key, Object: Identity<Key>>(private val pageSize: Int) {

    private val mapOfObjects : MutableMap<Key, Object> = mutableMapOf()
    private val liveDataOfObjects: MutableLiveData<Map<Key, Object>> = MutableLiveData(mapOfObjects)

    val allObjects : LiveData<List<Object>> = Transformations.map(liveDataOfObjects) { map ->
        map.values.toList()
    }

    private val dataSourceFactory = ObjectDataSourceFactory(mapOfObjects)

    val allObjectsPaged: LiveData<PagedList<Object>> =
            dataSourceFactory.toLiveData(pageSize)

    fun insert(`object`: Object) {
        mapOfObjects[`object`.getKey()] = `object`

        liveDataOfObjects.postValue(mapOfObjects)
        dataSourceFactory.invalidateSource()
    }

    fun insert(objects: List<Object>) {
        for (o in objects) {
            mapOfObjects[o.getKey()] = o
        }

        liveDataOfObjects.postValue(mapOfObjects)
        dataSourceFactory.invalidateSource()

    }

    fun update(`object`: Object) {
        insert(`object`)
    }

    fun update(objects: List<Object>) {
        insert(objects)
    }

    fun insertOrUpdate(`object`: Object) {
        insert(`object`)
    }

    fun insertOrUpdate(objects: List<Object>) {
        insert(objects)
    }

    fun delete(`object`: Object) {
        mapOfObjects.remove(`object`.getKey())

        liveDataOfObjects.postValue(mapOfObjects)
        dataSourceFactory.invalidateSource()
    }

}
