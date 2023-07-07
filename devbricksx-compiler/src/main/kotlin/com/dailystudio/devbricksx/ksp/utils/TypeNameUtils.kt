package com.dailystudio.devbricksx.ksp.utils

import com.dailystudio.devbricksx.ksp.helper.GeneratedNames
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName

object TypeNameUtils {

    fun typeOfKotlinAny(resolver: Resolver): KSClassDeclaration? {
        return resolver.getClassDeclarationByName<Any>()
    }

    fun isTypeOfAny(symbol: KSClassDeclaration): Boolean {
        return (symbol.toClassName() == ANY)
    }

    fun typeOf(resolver: Resolver, typeString: String): KSClassDeclaration? {
        return resolver.getClassDeclarationByName(typeString)
    }

    fun typeOfObject(packageName: String, typeName: String): ClassName {
        return ClassName(packageName, typeName)
    }

    fun typeOfCompanion(objectType: ClassName): ClassName {
        return typeOfCompanion(objectType.packageName, objectType.simpleName)
    }

    fun typeOfCompanion(packageName: String, typeName: String): ClassName {
        return ClassName(packageName, GeneratedNames.getRoomCompanionName(typeName))
    }
    fun typeOfContext(): ClassName {
        return ClassName("android.content", "Context")
    }

    fun typeOfApplication(): ClassName {
        return ClassName("android.app", "Application")
    }

    fun typeOfAndroidViewModel(): ClassName {
        return ClassName("androidx.lifecycle", "AndroidViewModel")
    }

    fun typeOfViewGroup(): ClassName {
        return ClassName("android.view", "ViewGroup")
    }

    fun typeOfLayoutManager() : ClassName {
        return ClassName("androidx.recyclerview.widget.RecyclerView", "LayoutManager")
    }

    fun typeOfLinearLayoutManager() : ClassName {
        return ClassName("androidx.recyclerview.widget", "LinearLayoutManager")
    }

    fun typeOfGridLayoutManager() : ClassName {
        return ClassName("androidx.recyclerview.widget", "GridLayoutManager")
    }

    fun typeOfView() : ClassName {
        return ClassName("android.view", "View")
    }

    fun typeOfBundle() : ClassName {
        return ClassName("android.os", "Bundle")
    }

    fun typeOfLayoutInflater(): ClassName {
        return ClassName("android.view", "LayoutInflater")
    }

    fun typeOfViewGroupLayoutParameter(): ClassName {
        return ClassName("android.view.ViewGroup", "LayoutParams")
    }

    fun typeOfLaunchMemberName() : MemberName {
        return MemberName("kotlinx.coroutines", "launch")
    }

    fun typeOfLaunchClassName() : ClassName {
        return ClassName("kotlinx.coroutines", "launch")
    }

    fun typeOfJob() : ClassName {
        return ClassName("kotlinx.coroutines", "Job")
    }

    fun typeOfDispatchers() : ClassName {
        return ClassName("kotlinx.coroutines", "Dispatchers")
    }

    fun typeOfShareIn(): ClassName {
        return ClassName("kotlinx.coroutines.flow", "shareIn")
    }

    fun typeOfSharingStarted(): ClassName {
        return ClassName("kotlinx.coroutines.flow", "SharingStarted")
    }

    fun typeOfLifecycleScope(): ClassName {
        return ClassName("androidx.lifecycle", "lifecycleScope")
    }

    fun typeOfRepeatOnLifecycle(): ClassName {
        return ClassName("androidx.lifecycle", "repeatOnLifecycle")
    }

    fun typeOfLifecycleState(): ClassName {
        return ClassName("androidx.lifecycle.Lifecycle", "State")
    }

    fun typeOfCollectLatest(): ClassName {
        return ClassName("kotlinx.coroutines.flow", "collectLatest")
    }

    fun typeOfObserver(): ClassName {
        return ClassName("androidx.lifecycle", "Observer")
    }

    fun typeOfViewModelScope(): ClassName {
        return ClassName("androidx.lifecycle", "viewModelScope")
    }

    fun typeOfViewModelProvider() : ClassName {
        return ClassName("androidx.lifecycle", "ViewModelProvider")
    }

    fun typeOfRoomDatabase(): ClassName {
        return ClassName("androidx.room", "RoomDatabase")
    }

    fun typeOfRoom(): ClassName {
        return ClassName("androidx.room", "Room")
    }

    fun typeOfRoomDatabaseBuilderOf(typeName: TypeName): TypeName {
        val builder = ClassName("androidx.room.RoomDatabase", "Builder")

        return builder.parameterizedBy(typeName)
    }

    fun typeOfMigration(): ClassName {
        return ClassName("androidx.room.migration", "Migration")
    }

    fun typeOfDummyMigration(): ClassName {
        return ClassName("com.dailystudio.devbricksx.database", "DummyMigration")
    }

    fun typeOfMapFunction(typeName1: TypeName, typeName2: TypeName): TypeName {
        val classNameOfMapFunction =
            ClassName("androidx.arch.core.util", "Function")
        return classNameOfMapFunction.parameterizedBy(
            typeName1, typeName2)
    }

    fun typeOfLifecycleMapFunction(): ClassName {
        return ClassName("androidx.lifecycle", "map")
    }

    fun typeOfFlowMapFunction(): TypeName {
        return ClassName("kotlinx.coroutines.flow", "map")
    }

    fun typeOfDataSourceFactory(): ClassName {
        return ClassName("androidx.paging.DataSource", "Factory")
    }

    fun typeOfDataSourceFactoryOf(typeName: TypeName): TypeName {
        val classNameODataSourceFactory = typeOfDataSourceFactory()

        return classNameODataSourceFactory.parameterizedBy(
            Integer::class.asClassName(), typeName)
    }

    fun typeOfPagingSource(): ClassName {
        return ClassName("androidx.paging", "PagingSource")
    }

    fun typeOfPagingSourceOf(typeName: TypeName): TypeName {
        val classNameOfPagingSource = typeOfPagingSource()

        return classNameOfPagingSource.parameterizedBy(
            INT, typeName)
    }

    fun typeOfPagingData(): ClassName {
        return ClassName("androidx.paging", "PagingData")
    }

    fun typeOfPagingDataOf(typeName: TypeName): TypeName {
        val pagingData = typeOfPagingData()

        return pagingData.parameterizedBy(typeName)
    }

    fun typeOfPageConfig(): Any {
        return ClassName("androidx.paging", "PagingConfig")
    }

    fun typeOfPager(): Any {
        return ClassName("androidx.paging", "Pager")
    }

    fun typeOfMutableList(): ClassName {
        return ClassName("kotlin.collections", "MutableList")
    }

    fun typeOfMutableListOf(typeName: TypeName): TypeName {
        val list = typeOfMutableList()

        return list.parameterizedBy(typeName)
    }

    fun typeOfLiveData(): ClassName {
        return ClassName("androidx.lifecycle", "LiveData")
    }

    fun typeOfLiveDataOf(typeName: TypeName): TypeName {
        val classNameOfLiveData = typeOfLiveData()

        return classNameOfLiveData.parameterizedBy(typeName)
    }

    fun typeOfAsLiveData(): ClassName {
        return ClassName("androidx.lifecycle", "asLiveData")
    }

    fun typeOfPagedListBuilder(): ClassName {
        return ClassName("androidx.paging", "LivePagedListBuilder")
    }

    fun typeOfPagedList(): ClassName {
        return ClassName("androidx.paging", "PagedList")
    }

    fun typeOfPagedListOf(typeName: TypeName): TypeName {
        val classNameOfPagedList = typeOfPagedList()

        return classNameOfPagedList.parameterizedBy(typeName)
    }

    fun typeOfTransformations(): ClassName {
        return ClassName("androidx.lifecycle", "Transformations")
    }

    fun typeOfFlow(): ClassName {
        return ClassName("kotlinx.coroutines.flow", "Flow")
    }

    fun typeOfFlowOf(typeName: TypeName): TypeName {
        val typeOfFlow = typeOfFlow()

        return typeOfFlow.parameterizedBy(typeName)
    }

    fun typeOfList(): ClassName {
        return ClassName("kotlin.collections", "List")
    }

    fun typeOfListOf(typeName: TypeName): TypeName {
        val list = typeOfList()

        return list.parameterizedBy(typeName)
    }

    fun typeOfArrayOf(typeName: TypeName): TypeName {
        return ARRAY.parameterizedBy(typeName)
    }

    fun typeOfInMemoryObject(): ClassName {
        return ClassName("com.dailystudio.devbricksx.inmemory","InMemoryObject")
    }

    fun typeOfInMemoryObjectManager(): ClassName {
        return ClassName("com.dailystudio.devbricksx.inmemory","InMemoryObjectManager")
    }

    fun typeOfObjectRepository(): ClassName {
        return ClassName("com.dailystudio.devbricksx.repository","ObjectRepository")
    }

    fun typeOfInMemoryObjectManagerOf(typeOfKey: TypeName,
                                      typeOfObject: TypeName): TypeName {
        val classNameOfManager = typeOfInMemoryObjectManager()

        return classNameOfManager.parameterizedBy(typeOfKey, typeOfObject)
    }

    fun typeOfObjectRepositoryOf(typeOfKey: TypeName,
                                 typeOfObject: TypeName): TypeName {
        val classNameOfRepo = typeOfObjectRepository()

        return classNameOfRepo.parameterizedBy(typeOfKey, typeOfObject)
    }

    fun typeOfItemCallback(): ClassName {
        return ClassName("androidx.recyclerview.widget.DiffUtil", "ItemCallback")
    }

    fun typeOfItemCallbackOf(typeName: TypeName): TypeName {
        val classNameOfItemCallback = typeOfItemCallback()

        return classNameOfItemCallback.parameterizedBy(typeName)
    }

    fun typeOfAbsListAdapter(): ClassName {
        return ClassName("com.dailystudio.devbricksx.ui", "AbsListAdapter")
    }

    fun typeOfAbsListAdapterOf(typeName: TypeName,
                               viewHolder: TypeName): TypeName {
        val listAdapter = typeOfAbsListAdapter()

        return listAdapter.parameterizedBy(typeName, viewHolder)
    }

    fun typeOfAbsPagingDataAdapter(): ClassName {
        return ClassName("com.dailystudio.devbricksx.ui", "AbsPagingDataAdapter")
    }

    fun typeOfAbsAbsPagingDataAdapterOf(typeName: TypeName,
                                        viewHolder: TypeName): TypeName {
        val listAdapter = typeOfAbsPagingDataAdapter()

        return listAdapter.parameterizedBy(typeName, viewHolder)
    }

    fun typeOfDevBricksXR(): ClassName {
        return ClassName("com.dailystudio.devbricksx", "R")
    }

    fun typeOfDevBricksXLogger(): ClassName {
        return ClassName("com.dailystudio.devbricksx.development", "Logger")
    }

    fun typeOfGlobalContextWrapper(): TypeName {
        return ClassName("com.dailystudio.devbricksx", "GlobalContextWrapper")
    }

    fun typeOfAbsRecyclerViewFragment(): ClassName {
        return ClassName("com.dailystudio.devbricksx.fragment", "AbsRecyclerViewFragment")
    }

    fun typeOfAbsNonRecyclableListViewFragment(): ClassName {
        return ClassName("com.dailystudio.devbricksx.fragment", "AbsNonRecyclableListViewFragment")
    }

    fun typeOfAbsViewPagerFragment(): ClassName {
        return ClassName("com.dailystudio.devbricksx.fragment", "AbsViewPagerFragment")
    }

    fun typeOfAbsPagingViewPagerFragment(): ClassName {
        return ClassName("com.dailystudio.devbricksx.fragment", "AbsPagingViewPagerFragment")
    }

    fun typeOfAdapterOf(typeName: TypeName): ClassName {
        require(typeName is ClassName)
        return typeOfAdapterOf(typeName.simpleName, typeName.packageName)
    }

    fun typeOfAdapterOf(className: String,
                        packageName: String) : ClassName {
        return ClassName(GeneratedNames.getAdapterPackageName(packageName),
            GeneratedNames.getAdapterName(className))
    }

    fun typeOfFragmentAdapterOf(typeName: TypeName): ClassName {
        require(typeName is ClassName)
        return typeOfFragmentAdapterOf(typeName.simpleName, typeName.packageName)
    }

    fun typeOfFragmentAdapterOf(className: String,
                                packageName: String) : ClassName {
        return ClassName(GeneratedNames.getAdapterPackageName(packageName),
            GeneratedNames.getFragmentAdapterName(className))
    }

    fun typeOfAbsFragmentStateAdapter(): ClassName {
        return ClassName("com.dailystudio.devbricksx.ui", "AbsFragmentStateAdapter")
    }

    fun typeOfAbsFragmentStateAdapterOf(typeName: TypeName): TypeName {
        val fragmentAdapter = typeOfAbsFragmentStateAdapter()

        return fragmentAdapter.parameterizedBy(typeName)
    }

    fun typeOfLifecycle(): ClassName {
        return ClassName("androidx.lifecycle", "Lifecycle")
    }

    fun typeOfFragmentManager(): ClassName {
        return ClassName("androidx.fragment.app", "FragmentManager")
    }

    fun typeOfAbsPrefs(): TypeName {
        return ClassName("com.dailystudio.devbricksx.preference", "AbsPrefs")
    }

    fun typeOfComposable(): ClassName {
        return ClassName("androidx.compose.runtime", "Composable")

    }

    fun typeOfNoLiveLiterals(): ClassName {
        return ClassName("androidx.compose.runtime", "NoLiveLiterals")

    }

    fun typeOfSuppressLintAnnotation(): ClassName {
        return ClassName("android.annotation", "SuppressLint")
    }

    fun defaultValOfType(typeName: TypeName): String {
        return if (typeName.isNullable) {
            return "null"
        } else {
            when (typeName) {
                STRING -> "\"\""
                INT, SHORT -> "0"
                LONG -> "0L"
                FLOAT -> "0f"
                DOUBLE -> "0.0"
                BOOLEAN -> "false"
                else -> {
                    ""
                }
            }
        }
    }

    fun findClassInSuperTypes(symbol: KSClassDeclaration,
                              className: ClassName
    ): KSType? {
        symbol.getAllSuperTypes().forEach {
            val classNameOfSupertype = it.toClassName()
            if (classNameOfSupertype == className) {
                return it
            }
        }

        return null
    }

}


