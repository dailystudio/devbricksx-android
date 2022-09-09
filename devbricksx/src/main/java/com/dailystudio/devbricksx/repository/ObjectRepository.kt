package com.dailystudio.devbricksx.repository

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagingSource
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.inmemory.InMemoryObjectManager
import kotlinx.coroutines.flow.Flow

open class ObjectRepository<Key: Comparable<Key>, Object: InMemoryObject<Key>>(
        private val manager: InMemoryObjectManager<Key, Object>,
        private val pageSize: Int) {

    val allObjects: List<Object>
        get() = manager.toList()
    val allObjectsLive : LiveData<List<Object>> = manager.toLiveData()
    val allObjectsLivePaged: LiveData<PagedList<Object>> =
        LivePagedListBuilder(manager.toDataSource(), pageSize).build()
    val allObjectsFlow: Flow<List<Object>> = manager.toFlow()
    val allObjectsPagingSource: PagingSource<Int, Object> get() =
        manager.toPagingSource()

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
