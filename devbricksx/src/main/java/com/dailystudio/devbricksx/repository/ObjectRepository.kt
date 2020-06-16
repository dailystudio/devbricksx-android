package com.dailystudio.devbricksx.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.inmemory.InMemoryObjectManager

open class ObjectRepository<Key: Comparable<Key>, Object: InMemoryObject<Key>>(
        private val manager: InMemoryObjectManager<Key, Object>,
        private val pageSize: Int) {

    private val liveDataOfObjects: MutableLiveData<InMemoryObjectManager<Key, Object>>
            = MutableLiveData(manager)

    val allObjects : LiveData<List<Object>> = manager.toLiveData()

    val allObjectsPaged: LiveData<PagedList<Object>> =
            LivePagedListBuilder(manager.toDataSource(), pageSize).build()

    fun get(key: Key): Object? {
        return manager.get(key)
    }

    fun insert(`object`: Object) {
        manager.add(`object`)
    }

    fun insert(objects: List<Object>) {
        manager.addAll(objects)
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
        manager.remove(`object`)
    }

}
