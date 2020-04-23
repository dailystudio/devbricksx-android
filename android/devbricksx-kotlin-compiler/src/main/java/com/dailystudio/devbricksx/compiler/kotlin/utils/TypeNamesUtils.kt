package com.dailystudio.devbricksx.compiler.kotlin.utils

import com.dailystudio.devbricksx.compiler.kotlin.GeneratedNames
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName

class TypeNamesUtils {

    companion object {

        fun getDevbrickxRTypeName(): ClassName {
            return ClassName("com.dailystudio.devbricksx", "R")
        }

        fun getViewTypeName() : ClassName {
            return ClassName("android.view", "View")
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

        fun getAbsRecyclerViewFragmentOfTypeName(dataTypeName: TypeName,
                                                 adapterTypeName: TypeName): TypeName {
            val fragment = getAbsRecyclerViewFragmentTypeName()

            return fragment.parameterizedBy(dataTypeName, adapterTypeName)
        }

        fun getViewGroupTypeName(): ClassName {
            return ClassName("android.view", "ViewGroup")
        }

        fun getLayoutInflaterTypeName(): ClassName {
            return ClassName("android.view", "LayoutInflater")
        }

        fun getViewModelScopeMemberName() : MemberName {
            return MemberName("androidx.lifecycle", "viewModelScope")
        }

        fun getLaunchMemberName() : MemberName {
            return MemberName("kotlinx.coroutines", "launch")
        }

        fun getJobTypeName() : ClassName {
            return ClassName.bestGuess("kotlinx.coroutines.Job")
        }

        fun getDispatchersTypeName() : ClassName {
            return ClassName.bestGuess("kotlinx.coroutines.Dispatchers")
        }

        fun getAndroidViewModelTypeName(): ClassName {
            return ClassName.bestGuess("androidx.lifecycle.AndroidViewModel")
        }

        fun getApplicationTypeName(): ClassName {
            return ClassName.bestGuess("android.app.Application")
        }

        fun getPageListAdapterTypeName(): ClassName {
            return ClassName("androidx.paging", "PagedListAdapter")
        }

        fun getPageListAdapterOfTypeName(`object`: TypeName,
                                         viewHolder: TypeName): TypeName {
            val listAdapter = getPageListAdapterTypeName()

            return listAdapter.parameterizedBy(`object`, viewHolder)
        }

        fun getListTypeName(): ClassName {
            return ClassName("kotlin.collections", "List")
        }

        fun getPagedListTypeName(): ClassName {
            return ClassName("androidx.paging", "PagedList")
        }

        fun getArrayListTypeName(): ClassName {
            return ClassName("kotlin.collections", "ArrayList")
        }

        fun getLiveDataTypeName(): ClassName {
            return ClassName("androidx.lifecycle", "LiveData")
        }

        fun getListOfTypeName(typeName: TypeName): TypeName {
            val list = getListTypeName()

            return list.parameterizedBy(typeName)
        }

        fun getLiveDataOfTypeName(typeName: TypeName): TypeName {
            val liveData = getLiveDataTypeName()

            return liveData.parameterizedBy(typeName)
        }

        fun getLiveDataOfListOfObjectTypeName(typeName: TypeName): TypeName {
            return getLiveDataOfTypeName(getListOfTypeName(typeName))
        }

        fun getPageListOfTypeName(typeClassName: TypeName): TypeName {
            val pagedList = getPagedListTypeName()

            return pagedList.parameterizedBy(typeClassName)
        }

        fun getLiveDataOfPagedListOfObjectsTypeName(typeName: TypeName): TypeName {
            return getLiveDataOfTypeName(getPageListOfTypeName(typeName))
        }

        fun getItemCallbackTypeName(): ClassName {
            return ClassName("androidx.recyclerview.widget.DiffUtil", "ItemCallback")
        }

        fun getItemCallbackOfTypeName(typeName: TypeName): TypeName {
            val itemCallback = getItemCallbackTypeName()

            return itemCallback.parameterizedBy(typeName)
        }

        fun getAdapterTypeName(className: String,
                               packageName: String) : ClassName {
            return ClassName(GeneratedNames.getAdapterPackageName(packageName),
                    GeneratedNames.getAdapterName(className))
        }

        fun getViewModelTypeName(className: String,
                                 packageName: String) : ClassName {
            return ClassName(GeneratedNames.getViewModelPackageName(packageName),
                    GeneratedNames.getViewModelName(className))
        }

    }
}