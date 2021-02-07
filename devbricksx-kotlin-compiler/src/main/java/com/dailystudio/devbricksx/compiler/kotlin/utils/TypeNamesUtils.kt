package com.dailystudio.devbricksx.compiler.kotlin.utils

import com.dailystudio.devbricksx.compiler.kotlin.GeneratedNames
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

class TypeNamesUtils {

    companion object {

        fun isTypeNameUnit(typeName: TypeName): Boolean {
            return getUnitTypeName() == typeName
        }

        fun getArrayTypeName(typeName: TypeName): TypeName {
            return ClassName("kotlin", "Array")
        }

        fun getUnitTypeName(): TypeName {
            return ClassName("kotlin", "Unit")
        }

        fun getDevbrickxRTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx", "R")
        }

        fun getViewTypeName() : ClassName {
            return ClassName("android.view", "View")
        }

        fun getViewGroupLayoutParameterTypeName(): ClassName {
            return ClassName("android.view.ViewGroup", "LayoutParams")
        }

        fun getBundleTypeName() : ClassName {
            return ClassName("android.os", "Bundle")
        }

        fun getViewModelProviderTypeName() : ClassName {
            return ClassName("androidx.lifecycle", "ViewModelProvider")
        }

        fun getLayoutManagerTypeName() : ClassName {
            return ClassName("androidx.recyclerview.widget.RecyclerView", "LayoutManager")
        }

        fun getLinearLayoutManagerTypeName() : ClassName {
            return ClassName("androidx.recyclerview.widget", "LinearLayoutManager")
        }

        fun getGridLayoutManagerTypeName() : ClassName {
            return ClassName("androidx.recyclerview.widget", "GridLayoutManager")
        }

        fun getAbsRecyclerViewFragmentTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx.fragment", "AbsRecyclerViewFragment")
        }

        fun getAbsPagingRecyclerViewFragmentTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx.fragment", "AbsPagingRecyclerViewFragment")
        }

        fun getAbsNonRecyclableListViewFragmentTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx.fragment", "AbsNonRecyclableListViewFragment")
        }

        fun getAbsViewPagerFragmentTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx.fragment", "AbsViewPagerFragment")
        }

        fun getViewModelScopeTypeName(): ClassName {
            return ClassName("androidx.lifecycle", "viewModelScope")
        }

        fun getViewModelScopeMemberName() : MemberName {
            return MemberName("androidx.lifecycle", "viewModelScope")
        }

        fun getFlowOnTypeName(): ClassName {
            return ClassName("kotlinx.coroutines.flow", "flowOn")
        }

        fun getShareInTypeName(): ClassName {
            return ClassName("kotlinx.coroutines.flow", "shareIn")
        }

        fun getSharingStartedTypeName(): ClassName {
            return ClassName("kotlinx.coroutines.flow", "SharingStarted")
        }

        fun getLifecycleScopeTypeName(): ClassName {
            return ClassName("androidx.lifecycle", "lifecycleScope")
        }

        fun getCollectLatestTypeName(): ClassName {
            return ClassName("kotlinx.coroutines.flow", "collectLatest")
        }

        fun getObserverTypeName(): ClassName {
            return ClassName("androidx.lifecycle", "Observer")
        }

        fun getAbsRecyclerViewFragmentOfTypeName(objectTypeName: TypeName,
                                                 dataTypeName: TypeName,
                                                 adapterTypeName: TypeName): TypeName {
            val fragment = getAbsRecyclerViewFragmentTypeName()

            return fragment.parameterizedBy(objectTypeName, dataTypeName, adapterTypeName)
        }

        fun getAbsViewPagerFragmentOfTypeName(objectTypeName: TypeName,
                                              dataTypeName: TypeName,
                                              adapterTypeName: TypeName): TypeName {
            val fragment = getAbsViewPagerFragmentTypeName()

            return fragment.parameterizedBy(objectTypeName, dataTypeName, adapterTypeName)
        }

        fun getAbsNonRecyclableListViewFragmentOfTypeName(objectTypeName: TypeName,
                                                          dataTypeName: TypeName,
                                                          adapterTypeName: TypeName): TypeName {
            val fragment = getAbsNonRecyclableListViewFragmentTypeName()

            return fragment.parameterizedBy(objectTypeName, dataTypeName, adapterTypeName)
        }

        fun getViewGroupTypeName(): ClassName {
            return ClassName("android.view", "ViewGroup")
        }

        fun getLayoutInflaterTypeName(): ClassName {
            return ClassName("android.view", "LayoutInflater")
        }

        fun getLaunchMemberName() : MemberName {
            return MemberName("kotlinx.coroutines", "launch")
        }

        fun getJobTypeName() : ClassName {
            return ClassName("kotlinx.coroutines", "Job")
        }

        fun getDispatchersTypeName() : ClassName {
            return ClassName("kotlinx.coroutines", "Dispatchers")
        }

        fun getAndroidViewModelTypeName(): ClassName {
            return ClassName("androidx.lifecycle", "AndroidViewModel")
        }

        fun getApplicationTypeName(): ClassName {
            return ClassName("android.app", "Application")
        }

        fun getLifecycleTypeName(): ClassName {
            return ClassName("androidx.lifecycle", "Lifecycle")
        }

        fun getFragmentManagerTypeName(): ClassName {
            return ClassName("androidx.fragment.app", "FragmentManager")
        }

        fun getPageListAdapterTypeName(): ClassName {
            return ClassName("androidx.paging", "PagedListAdapter")
        }

        fun getAbsPageListAdapterTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx.ui", "AbsPagedListAdapter")
        }

        fun getAbsPagingDataAdapterTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx.ui", "AbsPagingDataAdapter")
        }

        fun getAbsFragmentStateAdapterTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx.ui", "AbsFragmentStateAdapter")
        }

        fun getAbsListAdapterTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx.ui", "AbsListAdapter")
        }

        fun getPageListAdapterOfTypeName(`object`: TypeName,
                                         viewHolder: TypeName): TypeName {
            val listAdapter = getPageListAdapterTypeName()

            return listAdapter.parameterizedBy(`object`, viewHolder)
        }

        fun getAbsPageListAdapterOfTypeName(`object`: TypeName,
                                            viewHolder: TypeName): TypeName {
            val listAdapter = getAbsPageListAdapterTypeName()

            return listAdapter.parameterizedBy(`object`, viewHolder)
        }

        fun getAbsAbsPagingDataAdapterOfTypeName(`object`: TypeName,
                                                 viewHolder: TypeName): TypeName {
            val listAdapter = getAbsPagingDataAdapterTypeName()

            return listAdapter.parameterizedBy(`object`, viewHolder)
        }

        fun getAbsListAdapterOfTypeName(`object`: TypeName,
                                        viewHolder: TypeName): TypeName {
            val listAdapter = getAbsListAdapterTypeName()

            return listAdapter.parameterizedBy(`object`, viewHolder)
        }

        fun getAbsFragmentStateAdapterOfTypeName(`object`: TypeName): TypeName {
            val fragmentAdapter = getAbsFragmentStateAdapterTypeName()

            return fragmentAdapter.parameterizedBy(`object`)
        }

        fun getAbsPrefsTypeName(): TypeName {
            return ClassName("com.dailystudio.devbricksx.preference", "AbsPrefs")
        }

        fun getGlobalContextWrapperTypeName(): TypeName {
            return ClassName("com.dailystudio.devbricksx", "GlobalContextWrapper")
        }

        fun getJavaListTypeName() : ClassName {
            return ClassName("java.util", "List")
        }

        fun getJavaListOfTypeName(typeName: TypeName): TypeName {
            val list = getJavaListTypeName()

            return list.parameterizedBy(typeName)
        }

        fun getListTypeName(): ClassName {
            return ClassName("kotlin.collections", "List")
        }

        fun getMutableListTypeName(): ClassName {
            return ClassName("kotlin.collections", "MutableList")
        }

        fun getPagedListTypeName(): ClassName {
            return ClassName("androidx.paging", "PagedList")
        }

        fun getPagingSourceType(): ClassName {
            return ClassName("androidx.paging", "PagingSource")
        }

        fun getArrayListTypeName(): ClassName {
            return ClassName("kotlin.collections", "ArrayList")
        }

        fun getLiveDataTypeName(): ClassName {
            return ClassName("androidx.lifecycle", "LiveData")
        }

        fun getFlowTypeName(): ClassName {
            return ClassName("kotlinx.coroutines.flow", "Flow")
        }

        fun getAsLiveDataTypeName(): ClassName {
            return ClassName("androidx.lifecycle", "asLiveData")
        }

        fun getListOfTypeName(typeName: TypeName): TypeName {
            val list = getListTypeName()

            return list.parameterizedBy(typeName)
        }

        fun getMutableListOfTypeName(typeName: TypeName): TypeName {
            val list = getMutableListTypeName()

            return list.parameterizedBy(typeName)
        }

        fun getLiveDataOfTypeName(typeName: TypeName): TypeName {
            val liveData = getLiveDataTypeName()

            return liveData.parameterizedBy(typeName)
        }

        fun getFlowOfTypeName(typeName: TypeName): TypeName {
            val flow = getFlowTypeName()

            return flow.parameterizedBy(typeName)
        }

        fun getLiveDataOfListOfObjectTypeName(typeName: TypeName): TypeName {
            return getLiveDataOfTypeName(getListOfTypeName(typeName))
        }

        fun getLiveDataOfJavaListOfObjectTypeName(typeName: TypeName): TypeName {
            return getLiveDataOfTypeName(getJavaListOfTypeName(typeName))
        }

        fun getPageListOfTypeName(typeClassName: TypeName): TypeName {
            val pagedList = getPagedListTypeName()

            return pagedList.parameterizedBy(typeClassName)
        }

        fun getLiveDataOfPagedListOfObjectsTypeName(typeName: TypeName): TypeName {
            return getLiveDataOfTypeName(getPageListOfTypeName(typeName))
        }

        fun getLiveDataOfListOfObjectsTypeName(typeName: TypeName): TypeName {
            return getLiveDataOfTypeName(getListOfTypeName(typeName))
        }

        fun getFlowOfListOfObjectTypeName(typeName: TypeName): TypeName {
            return getFlowOfTypeName(getListOfTypeName(typeName))
        }

        fun getItemCallbackTypeName(): ClassName {
            return ClassName("androidx.recyclerview.widget.DiffUtil", "ItemCallback")
        }

        fun getItemCallbackOfTypeName(typeName: TypeName): TypeName {
            val itemCallback = getItemCallbackTypeName()

            return itemCallback.parameterizedBy(typeName)
        }

        fun getObjectRepositoryOfTypeName(keyTypeName: TypeName,
                                          objectTypeName: TypeName): TypeName {
            val repository = ClassName("com.dailystudio.devbricksx.repository",
            "ObjectRepository")

            return repository.parameterizedBy(keyTypeName, objectTypeName)
        }

        fun getObjectMangerOfTypeName(keyTypeName: TypeName,
                                      objectTypeName: TypeName): TypeName {
            val manager = ClassName("com.dailystudio.devbricksx.inmemory",
            "InMemoryObjectManager")

            return manager.parameterizedBy(keyTypeName, objectTypeName)
        }

        fun getAdapterTypeName(className: String,
                               packageName: String) : ClassName {
            return ClassName(GeneratedNames.getAdapterPackageName(packageName),
                    GeneratedNames.getAdapterName(className))
        }

        fun getFragmentAdapterTypeName(className: String,
                                       packageName: String) : ClassName {
            return ClassName(GeneratedNames.getAdapterPackageName(packageName),
                    GeneratedNames.getFragmentAdapterName(className))
        }

        fun getViewModelTypeName(className: String,
                                 packageName: String) : ClassName {
            return ClassName(GeneratedNames.getViewModelPackageName(packageName),
                    GeneratedNames.getViewModelName(className))
        }

        fun getJavaLongTypeName(): ClassName {
            return ClassName("java.lang", "Long")
        }

        fun getJavaIntegerTypeName(): ClassName {
            return ClassName("java.lang", "Integer")
        }

        fun getJavaStringTypeName(): ClassName {
            return ClassName("java.lang", "String")
        }

        fun getLongTypeName(): ClassName {
            return ClassName("kotlin", "Long")
        }

        fun getIntegerTypeName(): ClassName {
            return ClassName("kotlin", "Int")
        }

        fun getStringTypeName(): ClassName {
            return ClassName("kotlin", "String")
        }

        fun getBooleanTypeName(): ClassName {
            return ClassName("kotlin", "Boolean")
        }

        fun getFloatTypeName(): ClassName {
            return ClassName("kotlin", "Float")
        }

        fun getLoggerTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx.development", "Logger")
        }

        fun javaToKotlinTypeName(origTypeName: TypeName): TypeName {
            val javaString = TypeNamesUtils.getJavaStringTypeName()

            when (origTypeName) {
                javaString -> {
                    return TypeNamesUtils.getStringTypeName()
                }
            }

            return origTypeName
        }

    }
}