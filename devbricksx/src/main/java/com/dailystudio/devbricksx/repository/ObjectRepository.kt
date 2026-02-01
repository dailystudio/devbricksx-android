package com.dailystudio.devbricksx.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.paging.PagingSource
import com.dailystudio.devbricksx.inmemory.InMemoryObject
import com.dailystudio.devbricksx.inmemory.InMemoryObjectManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

/**
 * A generic repository for managing in-memory objects.
 *
 * It provides methods to access objects as List, LiveData, Flow, and PagingSource.
 * It delegates storage and retrieval to [InMemoryObjectManager].
 *
 * @param Key The type of the object's key.
 * @param Object The type of the in-memory object.
 * @property manager The manager backing this repository.
 * @property pageSize The page size for paging operations.
 */
open class ObjectRepository<Key: Comparable<Key>, Object: InMemoryObject<Key>>(
        private val manager: InMemoryObjectManager<Key, Object>,
        private val pageSize: Int) {

    /**
     * Gets all objects as a list.
     */
    val allObjects: List<Object>
        get() = manager.toList()

    /**
     * Gets all objects as a LiveData.
     */
    val allObjectsLive : LiveData<List<Object>> = manager.toLiveData()

    /**
     * Gets all objects as a Flow.
     */
    val allObjectsFlow: Flow<List<Object>> = manager.toFlow()

    /**
     * Gets a PagingSource for the objects.
     */
    val allObjectsPagingSource: PagingSource<Int, Object> get() =
        manager.toPagingSource()

    /**
     * Gets a single object by its key.
     *
     * @param key The key of the object.
     * @return The object, or null if not found.
     */
    fun getOne(key: Key): Object? {
        return manager.get(key)
    }

    /**
     * Gets a single object by its key as a LiveData.
     *
     * @param key The key of the object.
     * @return A LiveData containing the object, or null if not found.
     */
    fun getOneLive(key: Key): LiveData<Object?> {
        return getOneFlow(key).asLiveData()
    }

    /**
     * Gets a single object by its key as a Flow.
     *
     * @param key The key of the object.
     * @return A Flow emitting the object, or null if not found.
     */
    fun getOneFlow(key: Key): Flow<Object?> {
        return manager.toFlow().mapLatest { list ->
            list.firstOrNull { it.getKey() == key }
        }
    }

    /**
     * Inserts an object.
     *
     * @param object The object to insert.
     */
    fun insert(`object`: Object) {
        manager.add(`object`)
    }

    /**
     * Inserts a list of objects.
     *
     * @param objects The list of objects to insert.
     */
    fun insert(objects: List<Object>) {
        manager.addAll(objects)
    }

    /**
     * Updates an object.
     *
     * @param object The object to update.
     */
    fun update(`object`: Object) {
        insert(`object`)
    }

    /**
     * Updates a list of objects.
     *
     * @param objects The list of objects to update.
     */
    fun update(objects: List<Object>) {
        insert(objects)
    }

    /**
     * Inserts or updates an object.
     *
     * @param object The object to insert or update.
     */
    fun insertOrUpdate(`object`: Object) {
        insert(`object`)
    }

    /**
     * Inserts or updates a list of objects.
     *
     * @param objects The list of objects to insert or update.
     */
    fun insertOrUpdate(objects: List<Object>) {
        insert(objects)
    }

    /**
     * Deletes an object.
     *
     * @param object The object to delete.
     */
    fun delete(`object`: Object) {
        manager.remove(`object`)
    }

}
