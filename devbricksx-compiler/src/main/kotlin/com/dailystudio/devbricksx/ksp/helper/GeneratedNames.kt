package com.dailystudio.devbricksx.ksp.helper

import com.dailystudio.devbricksx.ksp.utils.pluralize
import java.util.*

object GeneratedNames {

    private const val COMPANION_PREFIX = "_"

    private const val DATABASE_SUFFIX = "Database"
    private const val DAO_SUFFIX = "Dao"
    private const val DIFF_UTIL_SUFFIX = "DiffUtil"
    private const val REPOSITORY_SUFFIX = "Repository"
    private const val MANAGER_SUFFIX = "Manager"
    private val DATABASE_PACKAGE_SUFFIXES = arrayOf(
        ".db",
        ".database",
        ".data"
    )
    private const val REPOSITORY_PACKAGE_SUFFIX = ".repository"
    private const val VIEW_MODEL_SUFFIX = "ViewModel"
    private const val VIEW_MODEL_PACKAGE_SUFFIX = ".model"
    private const val UI_PACKAGE_SUFFIX = ".ui"
    private const val ADAPTER_SUFFIX = "Adapter"
    private const val FRAGMENT_PACKAGE_SUFFIX = ".fragment"
    private const val LIST_FRAGMENT_SUFFIX = "ListFragment"
    private const val NON_RECYCLABLE_LIST_FRAGMENT_SUFFIX = "NonRecyclableListFragment"
    private const val PAGER_FRAGMENT_SUFFIX = "PagerFragment"
    private const val FRAGMENT_ADAPTER_SUFFIX = "FragmentAdapter"
    private const val SHARED_PREFS_SUFFIX = "Prefs"
    private const val PREF_KEY_PREFIX = "PREF_"
    private const val SCREEN_SUFFIX = "Screen"
    private const val COMPOSE_PACKAGE_SUFFIX = ".compose"

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

    private fun getPackageName(packageName: String,
                               suffix: String,
    ) : String {
        var basePackage = packageName

        for (packageSuffix in DATABASE_PACKAGE_SUFFIXES) {
            if (basePackage.endsWith(packageSuffix)) {
                basePackage = basePackage.removeSuffix(packageSuffix)
            }
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

    fun getComposePackageName(packageName: String) : String {
        return getPackageName(packageName, COMPOSE_PACKAGE_SUFFIX)
    }

    fun getAdapterName(className: String) : String {
        return buildString {
            this.append(className.pluralize().capitalizeName())
//            this.append('s')
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
            this.append(className.pluralize().capitalizeName())
//            this.append('s')
            this.append(LIST_FRAGMENT_SUFFIX)
        }
    }

    fun getNonRecyclableListFragmentName(className: String) : String {
        return buildString {
            this.append(className.pluralize().capitalizeName())
//            this.append('s')
            this.append(NON_RECYCLABLE_LIST_FRAGMENT_SUFFIX)
        }
    }

    fun getPagerFragmentName(className: String) : String {
        return buildString {
            this.append(className.pluralize().capitalizeName())
//            this.append('s')
            this.append(PAGER_FRAGMENT_SUFFIX)
        }
    }

    fun getFragmentAdapterName(className: String) : String {
        return buildString {
            this.append(className.pluralize().capitalizeName())
//            this.append('s')
            this.append(FRAGMENT_ADAPTER_SUFFIX)
        }
    }

    fun getSharedPrefsName(className: String) : String {
        return buildString {
            this.append(className)
            this.append(SHARED_PREFS_SUFFIX)
        }
    }

    fun getPreferenceKeyName(filedName: String): String {
        return buildString {
            append(PREF_KEY_PREFIX)
            append(filedName.underlineCaseName().uppercase())
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
            this.append(className.pluralize().lowerCamelCaseName())
//            this.append('s')
        }
    }

    fun getScreenName(className: String) : String {
        return buildString {
            this.append(className.pluralize())
            this.append(SCREEN_SUFFIX)
        }
    }

}