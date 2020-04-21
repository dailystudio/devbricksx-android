package com.dailystudio.devbricksx.compiler

import com.dailystudio.devbricksx.compiler.utils.lowerCamelCaseName

class GeneratedNames {
    companion object {
        private const val VIEW_MODEL_SUFFIX = "ViewModel"
        private const val REPOSITORY_SUFFIX = "Repository"
        private const val DATABASE_SUFFIX = "Database"
        private const val DAO_SUFFIX = "Dao"
        private const val DATABASE_PACKAGE_SUFFIX = ".db"
        private const val REPOSITORY_PACKAGE_SUFFIX = ".repository"

        fun getViewModelName(groupName: String) : String {
            return buildString {
                this.append(groupName.capitalize())
                this.append(VIEW_MODEL_SUFFIX)
            }
        }

        fun getRepositoryName(className: String) : String {
            return buildString {
                this.append(className)
                this.append(REPOSITORY_SUFFIX)
            }
        }

        fun getRepositoryPackageName(packageName: String) : String {
            if (!packageName.endsWith(DATABASE_PACKAGE_SUFFIX)) {
                return packageName
            }

            return buildString {
                this.append(packageName.removeSuffix(DATABASE_PACKAGE_SUFFIX))
                this.append(REPOSITORY_PACKAGE_SUFFIX)
            }
        }

        fun getDatabaseName(className: String) : String {
            return buildString {
                this.append(className)
                this.append(DATABASE_SUFFIX)
            }
        }

        fun getDaoVariableName(className: String) : String {
            return buildString {
                this.append(className)
                this.append(DAO_SUFFIX)
            }.lowerCamelCaseName()
        }

        fun getAllObjectsPropertyName(className: String) : String {
            return buildString {
                this.append("all")
                this.append(className)
                this.append("s")
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