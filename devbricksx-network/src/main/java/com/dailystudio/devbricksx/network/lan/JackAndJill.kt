package com.dailystudio.devbricksx.network.lan

object JackAndJill {

    private const val TYPE_SUFFIX = "._tcp."

    const val DEFAULT_TYPE = "_jackandjill"
    const val SERVICE_BASE_NAME = "JackAndJill"

    internal fun toNsdType(type: String): String {
        return buildString {
            append(type)
            append(TYPE_SUFFIX)
        }
    }
}
