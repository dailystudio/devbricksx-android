package com.dailystudio.devbricksx.inmemory

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.dailystudio.devbricksx.development.Logger
import java.lang.ref.WeakReference
import kotlin.math.min

interface InMemoryObject<Key: Comparable<Key>> {

    fun getKey(): Key

}

interface InMemoryObjectObserver {

    fun onChanged()

}

class InMemoryObjectsLiveData<Object: InMemoryObject<*>>(
        private val manager: InMemoryObjectManager<*, Object>)
    : LiveData<List<Object>>(), InMemoryObjectObserver {

    override fun onActive() {
        super.onActive()

        manager.addObserver(this)
    }

    override fun onInactive() {
        super.onInactive()

        manager.removeObserver(this)
    }

    override fun onChanged() {
        postValue(manager.toList())
    }

}

open class InMemoryObjectManager<Key: Comparable<Key>, Object : InMemoryObject<Key>> {

    private val mapOfObjects: MutableMap<Key, Object> = mutableMapOf()

    private val observerDelegates: MutableList<WeakReference<InMemoryObjectObserver>>
            = mutableListOf()

    private val liveDataSet: MutableList<LiveData<Map<Key, Object>>>
            = mutableListOf()

    fun add(`object`: Object) {
        synchronized(mapOfObjects) {
            Logger.debug("add object: $`object`")
            mapOfObjects[`object`.getKey()] = `object`
        }

        notifyObservers()
    }

    fun addAll(objects: List<Object>) {
        synchronized(mapOfObjects) {
            for (o in objects) {
                mapOfObjects[o.getKey()] = o
            }
        }

        notifyObservers()
    }

    fun remove(`object`: Object) {
        synchronized(mapOfObjects) {
            val key = `object`.getKey()
            if (!mapOfObjects.containsKey(key)) {
                return
            }

            mapOfObjects.remove(key)
        }

        notifyObservers()
    }

    fun removeByKey(key: Key): Object? {
        var `object`: Object? = null
        synchronized(mapOfObjects) {
            if (mapOfObjects.containsKey(key)) {
                `object` = mapOfObjects.remove(key)
            }
        }

        if (`object` != null) {
            notifyObservers()
        }

        return `object`
    }

    fun clear() {
        synchronized(mapOfObjects) {
            mapOfObjects.clear()
        }

        notifyObservers()
    }

    fun get(key: Key): Object? {
        synchronized(mapOfObjects) {
            if (!mapOfObjects.containsKey(key)) {
                return null
            }

            return mapOfObjects[key]
        }
    }

    fun toList(): List<Object> {
        synchronized(mapOfObjects) {
            return sortList(mapOfObjects.values.toList())
        }
    }

    protected open fun sortList(objects: List<Object>): List<Object> {
        return objects.sortedBy {
            it.getKey()
        }
    }

    fun addObserver(observer: InMemoryObjectObserver) {
        synchronized(observerDelegates) {
            observerDelegates.add(WeakReference(observer))
        }
    }

    fun removeObserver(observer: InMemoryObjectObserver) {
        synchronized(observerDelegates) {
            for (delegate in observerDelegates) {
                val o = delegate.get()
                if (o == observer) {
                    observerDelegates.remove(delegate)
                }
            }
        }
    }

    private fun notifyObservers() {
        synchronized(observerDelegates) {
            var uselessDelegates: MutableList<WeakReference<InMemoryObjectObserver>> =
                    mutableListOf()
            for (delegate in observerDelegates) {
                val observer = delegate.get()
                if (observer == null) {
                    uselessDelegates.add(delegate)
                } else {
                    observer.onChanged()
                }
            }

            for (delegate in uselessDelegates) {
                observerDelegates.remove(delegate)
            }
        }
    }

    fun toLiveData(): LiveData<List<Object>> {
        return InMemoryObjectsLiveData(this)
    }

    fun toDataSource(): DataSource.Factory<Long, Object> {
        return InMemoryObjectDataSourceFactory(this)
    }

}

class InMemoryObjectDataSource<Object: InMemoryObject<*>>(
        private val manager: InMemoryObjectManager<*, Object>)
    : PageKeyedDataSource<Long, Object>(), InMemoryObjectObserver {

    init {
        manager.addObserver(this)
    }

    override fun loadInitial(params: LoadInitialParams<Long>,
                             callback: LoadInitialCallback<Long, Object>) {
        val listOfObjects = manager.toList()

        val start = 0
        val end = min(listOfObjects.size, params.requestedLoadSize)
        Logger.debug("start: $start, end: $end")

        val next = if (end < listOfObjects.size) end else null

        callback.onResult(listOfObjects.subList(start, end), null, next?.toLong())
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Object>) {
        val listOfObjects = manager.toList()

        val start = params.key
        val end = min(listOfObjects.size.toLong(), start + params.requestedLoadSize)
        Logger.debug("start: $start, end: $end")

        val next = if (end < listOfObjects.size) end else null

        callback.onResult(listOfObjects.subList(start.toInt(), end.toInt()), next)
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Object>) {
    }

    override fun onChanged() {
        invalidate()
    }

}

class InMemoryObjectDataSourceFactory<Object: InMemoryObject<*>>(
        private val manager: InMemoryObjectManager<*, Object>)
    : DataSource.Factory<Long, Object>() {

    override fun create(): DataSource<Long, Object> {
        return InMemoryObjectDataSource(manager)
    }

}
