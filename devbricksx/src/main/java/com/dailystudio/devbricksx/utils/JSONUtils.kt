package com.dailystudio.devbricksx.utils

import android.content.Context
import com.dailystudio.devbricksx.development.Logger
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import java.lang.Exception

/**
 * Utility class for JSON operations using Gson.
 */
object JSONUtils {

    private val GSON = Gson()

    /**
     * Deserializes a JSON string to an object.
     *
     * @param jsonStr The JSON string.
     * @param objectClass The class of the object to deserialize to.
     * @param adapters Optional map of custom JsonDeserializers.
     * @return The deserialized object, or null if parsing fails.
     */
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

    /**
     * Deserializes a JSON file to an object.
     *
     * @param file The path to the JSON file.
     * @param objectClass The class of the object to deserialize to.
     * @param adapters Optional map of custom JsonDeserializers.
     * @return The deserialized object, or null if reading or parsing fails.
     */
    fun <Object> fromFile(file: String,
                          objectClass: Class<Object>,
                          adapters: Map<Class<*>, JsonDeserializer<*>>? = null): Object? {
        val json = FileUtils.fileToString(file) ?: return null
        if (json.isBlank()) {
            return null
        }

        return fromString(json, objectClass, adapters)
    }

    /**
     * Deserializes a JSON file from assets to an object.
     *
     * @param context The context.
     * @param file The asset file path.
     * @param objectClass The class of the object to deserialize to.
     * @param adapters Optional map of custom JsonDeserializers.
     * @return The deserialized object, or null if reading or parsing fails.
     */
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

    /**
     * Deserializes a JSON file from raw resources to an object.
     *
     * @param context The context.
     * @param resId The raw resource ID.
     * @param objectClass The class of the object to deserialize to.
     * @param adapters Optional map of custom JsonDeserializers.
     * @return The deserialized object, or null if reading or parsing fails.
     */
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