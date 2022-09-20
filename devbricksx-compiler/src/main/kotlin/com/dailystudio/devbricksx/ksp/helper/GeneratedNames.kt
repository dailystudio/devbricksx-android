package com.dailystudio.devbricksx.ksp.helper

import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.util.*
import javax.lang.model.element.VariableElement

object GeneratedNames {

    private const val COMPANION_PREFIX = "_"

    private const val DATABASE_SUFFIX = "Database"
    private const val DAO_SUFFIX = "Dao"
    private const val DIFF_UTIL_SUFFIX = "DiffUtil"
    private const val REPOSITORY_SUFFIX = "Repository"
    private const val MANAGER_SUFFIX = "Manager"
    private const val DATABASE_PACKAGE_SUFFIX = ".db"
    private const val REPOSITORY_PACKAGE_SUFFIX = ".repository"
    private const val VIEW_MODEL_SUFFIX = "ViewModel"
    private const val VIEW_MODEL_PACKAGE_SUFFIX = ".model"
    private const val UI_PACKAGE_SUFFIX = ".ui"
    private const val ADAPTER_SUFFIX = "Adapter"
    private const val FRAGMENT_PACKAGE_SUFFIX = ".fragment"
    private const val LIST_FRAGMENT_SUFFIX = "ListFragment"
    private const val NON_RECYCLABLE_LIST_FRAGMENT_SUFFIX = "NonRecyclableListFragment"
    private const val PAGER_FRAGMENT_SUFFIX = "PagerFragment"

    const val KOTLIN_COMPANION_OBJECT_FIELD = "Companion"

    fun nameOfFuncForCompanion(nameOfFunc: String): String {
        return buildString {
            append("_")
            append(nameOfFunc)
        }
    }

    fun nameOfParamInFuncForCompanion(nameOfParam: String): String {
        return buildString {
            append("_")
            append(nameOfParam)
        }
    }

    fun getTableName(className: String): String {
        return className.lowercase(Locale.getDefault())
    }

    fun getDaoExtensionCompanionName(className: String?): String {
        val builder = StringBuilder(className)
        builder.insert(0, COMPANION_PREFIX)
        return builder.toString()
    }

    fun getRoomCompanionName(className: String?): String {
        val builder = StringBuilder(className)
        builder.insert(0, COMPANION_PREFIX)
        return builder.toString()
    }

    fun getDiffUtilName(className: String?): String {
        val builder = StringBuilder(className)
        builder.append(DIFF_UTIL_SUFFIX)
        return builder.toString()
    }

    fun getRoomCompanionDaoName(className: String?): String {
        val builder = StringBuilder(className)
        builder.append(DAO_SUFFIX)
        return builder.toString()
    }

    fun getRoomCompanionRepositoryName(className: String?): String {
        val builder = StringBuilder(className)
        builder.append(REPOSITORY_SUFFIX)
        return builder.toString()
    }

    fun getRoomCompanionRepositoryPackageName(packageName: String): String {
        var packageName = packageName
        if (packageName.isEmpty()) {
            return packageName
        }

        if (packageName.endsWith(DATABASE_PACKAGE_SUFFIX)) {
            packageName = packageName.substring(
                0,
                packageName.length - DATABASE_PACKAGE_SUFFIX.length
            )
        }

        val builder = StringBuilder(packageName)
        builder.append(REPOSITORY_PACKAGE_SUFFIX)
        return builder.toString()
    }

    fun getRoomCompanionDatabaseName(className: String?): String {
        val builder = StringBuilder(className)
        builder.append(DATABASE_SUFFIX)
        return builder.toString()
    }

    fun databaseToClassName(database: String): String {
        if (database.isEmpty()) {
            return DATABASE_SUFFIX
        }
        val builder: StringBuilder = StringBuilder(database.capitalizeName())
        if (!database.endsWith(DATABASE_SUFFIX)) {
            builder.append(DATABASE_SUFFIX)
        }

        return builder.toString()
    }

    fun getRepositoryName(className: String) : String {
        return buildString {
            this.append(className)
            this.append(REPOSITORY_SUFFIX)
        }
    }

    fun getManagerName(className: String) : String {
        return buildString {
            this.append(className)
            this.append(MANAGER_SUFFIX)
        }
    }

    private fun getPackageName(packageName: String, suffix: String) : String {
        var basePackage = if (packageName.endsWith(DATABASE_PACKAGE_SUFFIX)) {
            packageName.removeSuffix(DATABASE_PACKAGE_SUFFIX)
        } else {
            packageName
        }

        return buildString {
            this.append(basePackage)
            this.append(suffix)
        }
    }

    fun getRepositoryPackageName(packageName: String) : String {
        return getPackageName(packageName, REPOSITORY_PACKAGE_SUFFIX)
    }

    fun getViewModelName(className: String) : String {
        return buildString {
            this.append(className.capitalizeName())
            this.append(VIEW_MODEL_SUFFIX)
        }
    }

    fun getViewModelPackageName(packageName: String) : String {
        return getPackageName(packageName, VIEW_MODEL_PACKAGE_SUFFIX)
    }

    fun getAdapterName(className: String) : String {
        return buildString {
            this.append(className.capitalizeName())
            this.append('s')
            this.append(ADAPTER_SUFFIX)
        }
    }

    fun getAdapterPackageName(packageName: String) : String {
        return getPackageName(packageName, UI_PACKAGE_SUFFIX)
    }

    fun getFragmentPackageName(packageName: String) : String {
        return getPackageName(packageName, FRAGMENT_PACKAGE_SUFFIX)
    }

    fun getListFragmentName(className: String) : String {
        return buildString {
            this.append(className.capitalizeName())
            this.append('s')
            this.append(LIST_FRAGMENT_SUFFIX)
        }
    }

    fun getDaoVariableName(className: String) : String {
        return buildString {
            this.append(className)
            this.append(DAO_SUFFIX)
        }.lowerCamelCaseName()
    }

    fun getObjectVariableName(className: String): String {
        return buildString {
            this.append(className.lowerCamelCaseName())
        }
    }

    fun getObjectsVariableName(className: String): String {
        return buildString {
            this.append(className.lowerCamelCaseName())
            this.append('s')
        }
    }

}