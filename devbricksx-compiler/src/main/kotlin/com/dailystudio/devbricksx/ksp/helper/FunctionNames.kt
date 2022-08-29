package com.dailystudio.devbricksx.ksp.helper

enum class FunctionNames {

    GET_ONE,
    GET_ONE_LIVE,
    GET_ALL,
    GET_ALL_LIVE,
    GET_ALL_DATA_SOURCE,
    GET_ALL_FLOW,
    GET_ALL_LIVE_PAGED,
    GET_ALL_PAGING_SOURCE,

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

    fun nameOfFuncForCompanion(): String {
        return toWrappedFunc(nameOfFunc())
    }

}