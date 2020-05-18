package com.dailystudio.devbricksx.utils

import android.content.Context
import com.dailystudio.devbricksx.development.Logger
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.lang.Exception

object JSONUtils {

    private val GSON = Gson()

    fun <Object> fromString(jsonStr: String,
                            objectClass: Class<Object>,
                            adapters: Map<Class<*>, JsonDeserializer<*>>? = null): Object? {
        Logger.debug("json: $jsonStr")

        val gson = if (adapters == null) GSON else {
            val builder = GsonBuilder()
            for ((k, a) in adapters) {
                builder.registerTypeAdapter(k, a)
            }

            builder.create()
        }

        return try {
            gson.fromJson(jsonStr, objectClass)
        } catch (e: Exception) {
            Logger.error("parse json object from [$jsonStr] failed: $e")
            null
        }
    }

    fun <Object> fromFile(file: String,
                          objectClass: Class<Object>,
                          adapters: Map<Class<*>, JsonDeserializer<*>>? = null): Object? {
        val json = FileUtils.fileToString(file) ?: return null
        if (json.isBlank()) {
            return null
        }

        return fromString(json, objectClass, adapters)
    }

    fun <Object> fromAsset(context: Context,
                           file: String,
                           objectClass: Class<Object>,
                           adapters: Map<Class<*>, JsonDeserializer<*>>? = null): Object? {
        val json = FileUtils.assetToString(context, file) ?: return null
        if (json.isBlank()) {
            return null
        }

        return fromString(json, objectClass, adapters)
    }

    fun <Object> fromRaw(context: Context,
                         resId: Int,
                         objectClass: Class<Object>,
                         adapters: Map<Class<*>, JsonDeserializer<*>>? = null): Object? {
        val json = FileUtils.rawToString(context, resId) ?: return null
        if (json.isBlank()) {
            return null
        }

        return fromString(json, objectClass, adapters)
    }

}