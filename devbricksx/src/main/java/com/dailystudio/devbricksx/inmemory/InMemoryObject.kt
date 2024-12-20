package com.dailystudio.devbricksx.inmemory

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dailystudio.devbricksx.development.Logger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
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

        /*
         * If we do not post value here, initial data will be missed
         * when all of changes have happened before onActive() is called.
         *
         * If we post value each when onActive() is called, there be
         * duplicated update and cause extra UI refresh, especially when
         * activity of fragment resumes.
         *
         * So, we should check the value and perform the proper calls.
         */
        Logger.debug("livedata: $this, current value: $value")
        if (value == null) {
            val initValue = manager.toList()
            Logger.debug("post value at initialization: $initValue")

            postValue(initValue)
        }

        manager.addObserver(this)
    }

    override fun onInactive() {
        super.onInactive()

        manager.removeObserver(this)
    }

    override fun onChanged() {
        val data = manager.toList()

        postValue(data)
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
            var targetDelegate: WeakReference<InMemoryObjectObserver>? = null

            for (delegate in observerDelegates) {
                val o = delegate.get()
                if (o == observer) {
                    targetDelegate = delegate
                }
            }

            targetDelegate?.let {
                observerDelegates.remove(it)
            }
        }
    }

    private fun notifyObservers() {
        synchronized(observerDelegates) {
            val uselessDelegates: MutableList<WeakReference<InMemoryObjectObserver>> =
                    mutableListOf()
            for (delegate in observerDelegates) {
                val observer = delegate.get()
                if (observer == null) {
                    uselessDelegates.add(delegate)
                } else {
//                    Logger.debug("notify observer: $observer")
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

    fun toPagingSource(): PagingSource<Int, Object> {
        return InMemoryObjectPagingSource(this)
    }

    fun toFlow(): Flow<List<Object>> = flow {
        Logger.debug("flow[${this.hashCode()}] is created.")
        val channel = Channel<List<Object>>(Channel.UNLIMITED)

        val observer = object: InMemoryObjectObserver {
            override fun onChanged() {
                val data = toList()

                channel.trySend(data)
            }
        }

        emit(this@InMemoryObjectManager.toList())
        this@InMemoryObjectManager.addObserver(observer)

        try {
            channel.consumeEach {
                emit(it)
            }
        } finally {
            Logger.debug("flow[${this.hashCode()}] is destroyed.")
            this@InMemoryObjectManager.removeObserver(observer)
        }
    }

}

class InMemoryObjectPagingSource<Object: InMemoryObject<*>>(
        private val manager: InMemoryObjectManager<*, Object>)
    : PagingSource<Int, Object>(), InMemoryObjectObserver {

    companion object {
        var DEBUG_PAGING_3 = true
    }

    init {
        manager.addObserver(this)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Object> {
        if (DEBUG_PAGING_3) {
            Logger.debug("[PG3] params: ${params.javaClass.simpleName}, [key: ${params.key}, loadSize: ${params.loadSize}]")
        }

        val listOfObjects = manager.toList()

        val start = params.key ?: 0
        val end = min(listOfObjects.size, start + params.loadSize)

        val prevKey = null
        val nextKey = if (end >= listOfObjects.size - 1) {
            null
        } else {
            end
        }

        if (DEBUG_PAGING_3) {
            Logger.debug("[PG3] start: $start, end: $end, [prev: $prevKey, next: $nextKey]")
        }

        return LoadResult.Page(
                data = listOfObjects.subList(start, end),
                prevKey = null,
                nextKey = nextKey
        )
    }

    override fun onChanged() {
        invalidate()
    }

    override fun getRefreshKey(state: PagingState<Int, Object>): Int? {
        if (DEBUG_PAGING_3) {
            Logger.debug("[PG3]: state.pages = ${state.pages.size}")
            Logger.debug("[PG3]: state.anchorPosition = ${state.anchorPosition}")
            Logger.debug("[PG3]: state.config.initialLoadSize = ${state.config.initialLoadSize}")
            Logger.debug("[PG3]: state.config.jumpThreshold = ${state.config.jumpThreshold}")
            Logger.debug("[PG3]: state.config.anchorPosition = ${state.config.maxSize}")
            Logger.debug("[PG3]: state.config.pageSize = ${state.config.pageSize}")
            Logger.debug("[PG3]: state.config.prefetchDistance = ${state.config.prefetchDistance}")
            Logger.debug("[PG3]: state.config.enablePlaceholders = ${state.config.enablePlaceholders}")
        }
        val refreshKey = state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            if (DEBUG_PAGING_3) {
                Logger.debug("[PG3]: anchorPage.prevKey = ${anchorPage?.prevKey}")
                Logger.debug("[PG3]: anchorPage.nextKey = ${anchorPage?.nextKey}")
                Logger.debug("[PG3]: anchorPage.data.size = ${anchorPage?.data?.size}")
            }

            anchorPage?.prevKey?.plus(state.config.pageSize) ?: anchorPage?.nextKey?.minus(anchorPage.data.size)
        }

        if (DEBUG_PAGING_3) {
            Logger.debug("[PG3]: refreshKey = $refreshKey")
        }

        return refreshKey
    }

}