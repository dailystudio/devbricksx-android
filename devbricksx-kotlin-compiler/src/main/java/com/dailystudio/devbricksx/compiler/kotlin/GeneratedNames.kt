package com.dailystudio.devbricksx.compiler.kotlin

import com.dailystudio.devbricksx.compiler.kotlin.utils.lowerCamelCaseName

class GeneratedNames {
    companion object {
        private const val VIEW_MODEL_SUFFIX = "ViewModel"
        private const val ADAPTER_SUFFIX = "Adapter"
        private const val FRAGMENT_ADAPTER_SUFFIX = "FragmentAdapter"
        private const val LIST_FRAGMENT_SUFFIX = "ListFragment"
        private const val PAGER_FRAGMENT_SUFFIX = "PagerFragment"
        private const val REPOSITORY_SUFFIX = "Repository"
        private const val MANAGER_SUFFIX = "Manager"
        private const val DATABASE_SUFFIX = "Database"
        private const val DAO_SUFFIX = "Dao"
        private const val DIFF_UTIL_SUFFIX = "DiffUtil"
        private const val SHARED_PREFS_SUFFIX = "Prefs"

        private const val DATABASE_PACKAGE_SUFFIX = ".db"
        private const val REPOSITORY_PACKAGE_SUFFIX = ".repository"
        private const val VIEW_MODEL_PACKAGE_SUFFIX = ".model"
        private const val UI_PACKAGE_SUFFIX = ".ui"
        private const val FRAGMENT_PACKAGE_SUFFIX = ".fragment"

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

        fun getViewModelName(className: String) : String {
            return buildString {
                this.append(className.capitalize())
                this.append(VIEW_MODEL_SUFFIX)
            }
        }

        fun getViewModelPackageName(packageName: String) : String {
            return getPackageName(packageName, VIEW_MODEL_PACKAGE_SUFFIX)
        }

        fun getAdapterName(className: String) : String {
            return buildString {
                this.append(className.capitalize())
                this.append('s')
                this.append(ADAPTER_SUFFIX)
            }
        }

        fun getFragmentAdapterName(className: String) : String {
            return buildString {
                this.append(className.capitalize())
                this.append('s')
                this.append(FRAGMENT_ADAPTER_SUFFIX)
            }
        }

        fun getListFragmentName(className: String) : String {
            return buildString {
                this.append(className.capitalize())
                this.append('s')
                this.append(LIST_FRAGMENT_SUFFIX)
            }
        }

        fun getPagerFragmentName(className: String) : String {
            return buildString {
                this.append(className.capitalize())
                this.append('s')
                this.append(PAGER_FRAGMENT_SUFFIX)
            }
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

        fun getRepositoryPackageName(packageName: String) : String {
            return getPackageName(packageName, REPOSITORY_PACKAGE_SUFFIX)
        }

        fun getAdapterPackageName(packageName: String) : String {
            return getPackageName(packageName, UI_PACKAGE_SUFFIX)
        }

        fun getFragmentPackageName(packageName: String) : String {
            return getPackageName(packageName, FRAGMENT_PACKAGE_SUFFIX)
        }

        fun getDiffUtilName(className: String) : String {
            return buildString {
                this.append(className)
                this.append(DIFF_UTIL_SUFFIX)
            }
        }

        fun getSharedPrefsName(className: String) : String {
            return buildString {
                this.append(className)
                this.append(SHARED_PREFS_SUFFIX)
            }
        }

        fun getDatabaseName(className: String) : String {
            return buildString {
                this.append(className.capitalize())
                this.append(DATABASE_SUFFIX)
            }
        }

        fun getDaoVariableName(className: String) : String {
            return buildString {
                this.append(className)
                this.append(DAO_SUFFIX)
            }.lowerCamelCaseName()
        }

        fun getAllObjectsMethodName(className: String) : String {
            return buildString {
                this.append("get")
                this.append(className)
                this.append("s")
            }
        }

        fun getAllObjectsRepoMethodName(className: String) : String {
            return buildString {
                this.append("getAll")
                this.append(className)
                this.append("s")
            }
        }

        fun getAllObjectsLivePropertyName(className: String) : String {
            return buildString {
                this.append("all")
                this.append(className)
                this.append("s")
                this.append("Live")
            }
        }

        fun getAllObjectsPagedPropertyName(className: String) : String {
            return buildString {
                this.append("all")
                this.append(className)
                this.append("s")
                this.append("Paged")
            }
        }

        fun getPluralMethodName(methodName: String,
                                className: String): String {
            return buildString {
                this.append(methodName)
                this.append(className.capitalize())
                this.append('s')
            }
        }

        fun getMethodName(methodName: String,
                          className: String): String {
            return buildString {
                this.append(methodName)
                this.append(className.capitalize())
            }
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
}