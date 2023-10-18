package com.dailystudio.devbricksx.ksp.helper

enum class FunctionNames {

    GET_ONE,
    GET_ONE_LIVE,
    GET_ONE_FLOW,
    GET_ALL,
    GET_ALL_LIVE,
    GET_ALL_DATA_SOURCE,
    GET_ALL_FLOW,
    GET_ALL_LIVE_PAGED,
    GET_ALL_PAGING_SOURCE,
    GET_ALL_PAGING_DATA,

    INSERT,
    UPDATE,
    INSERT_OR_UPDATE,
    DELETE;

    companion object {

        fun toWrappedFunc(nameOfFunc: String): String {
            return buildString {
                append('_')
                append(nameOfFunc)
            }
        }

        fun toWrapperFunc(nameOfFunc: String): String {
            return nameOfFunc.removePrefix("_")
        }

        fun nameOfParamInWrappedFunc(nameOfParam: String): String {
            return buildString {
                append(nameOfParam)
                append('_')
            }
        }
    }

    fun nameOfFunc(): String {
        val parts = this.name.split("_")
        return buildString {
            for ((i, part) in parts.withIndex()) {
                if (i == 0) {
                    append(part.lowercase())
                } else {
                    append(part.lowercase().capitalizeName())
                }
            }
        }
    }


    fun nameOfFuncForType(typeName: String): String {
        return nameOfFuncForType(typeName, false)
    }

    fun nameOfFuncForType(typeName: String, plural: Boolean): String {
        val nameOfFunc = nameOfFunc()
        return if (nameOfFunc.contains("getOne")) {
            nameOfFunc.replaceFirst("getOne", "get${typeName}")
        } else if (nameOfFunc.contains("getAll")) {
            nameOfFunc.replaceFirst("getAll", "getAll${typeName}s")
        } else {
            buildString {
                append(nameOfFunc)
                append(typeName.capitalizeName())
                if (plural) {
                    append('s')
                }
            }
        }
    }

    fun nameOfPropFuncForType(typeName: String): String {
        val nameOfFunc = nameOfFunc()
        return if (nameOfFunc.contains("getOne")) {
            nameOfFunc.replaceFirst("getOne", "${typeName.lowerCamelCaseName()}")
        } else if (nameOfFunc.contains("getAll")) {
            nameOfFunc.replaceFirst("getAll", "all${typeName}s")
        } else {
            throw Exception("prop is NOT supported on input function.")
        }
    }


    fun nameOfFuncForCompanion(): String {
        return toWrappedFunc(nameOfFunc())
    }

}