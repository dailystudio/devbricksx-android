package com.dailystudio.devbricksx.ksp.helper

import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.util.*
import javax.lang.model.element.VariableElement

object GeneratedNames {

     const val COMPANION_PREFIX = "_"

     const val DATABASE_SUFFIX = "Database"
     const val DAO_SUFFIX = "Dao"
     const val DIFF_UTIL_SUFFIX = "DiffUtil"
     const val REPOSITORY_SUFFIX = "Repository"
     const val DATABASE_PACKAGE_SUFFIX = ".db"
     const val REPOSITORY_PACKAGE_SUFFIX = ".repository"

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

    fun getRepositoryObjectMethodName(className: String?): String {
        val builder = StringBuilder("get")
        builder.append(className)
        return builder.toString()
    }

    fun getRepositoryObjectLiveMethodName(className: String?): String {
        val builder = StringBuilder("get")
        builder.append(className)
        builder.append("Live")
        return builder.toString()
    }

    fun getRepositoryAllObjectsMethodName(className: String?): String {
        val builder = StringBuilder("getAll")
        builder.append(className)
        builder.append('s')
        return builder.toString()
    }

    fun getRepositoryAllObjectsLiveMethodName(className: String?): String {
        val builder = StringBuilder("getAll")
        builder.append(className)
        builder.append('s')
        builder.append("Live")
        return builder.toString()
    }

    fun getRepositoryAllObjectsPagedMethodName(className: String?): String {
        val builder = StringBuilder("getAll")
        builder.append(className)
        builder.append('s')
        builder.append("Paged")
        return builder.toString()
    }

    fun getRepositoryAllObjectsFlowMethodName(className: String?): String {
        val builder = StringBuilder("getAll")
        builder.append(className)
        builder.append('s')
        builder.append("Flow")
        return builder.toString()
    }

    fun getRepositoryAllObjectsPagingSourceMethodName(className: String?): String {
        val builder = StringBuilder("getAll")
        builder.append(className)
        builder.append('s')
        builder.append("PagingSource")
        return builder.toString()
    }
}