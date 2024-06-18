/*
 * Copyright (c) 2024. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.russhwolf.settings.Settings
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import me.gustavolopezxyz.desktop.Config
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute

class FileSettings(private val storagePath: File, private val prefix: String = "") : Settings {
    constructor(config: Config) : this(Path(config.dataDir, "settings.json").absolute().toFile())

    private val cache = mutableMapOf<String, String>()
    private var loaded = false

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadIfNeeded() {
        if (loaded) return

        if (storagePath.exists()) cache.putAll(Json.decodeFromStream<Map<String, String>>(storagePath.inputStream()))

        loaded = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun persist() {
        if (!loaded) return

        Json.encodeToStream(cache, storagePath.outputStream())
    }


    private inline fun <reified T> getValueOrNull(key: String): T? {
        loadIfNeeded()

        cache[key].let {
            return if (it == null) null else Json.decodeFromString<T>(it)
        }
    }

    private inline fun <reified T> getValueOrDefault(key: String, defaultValue: T): T {
        loadIfNeeded()

        cache[key].let {
            return if (it == null) {
                defaultValue
            } else {
                Json.decodeFromString<T>(it)
            }
        }
    }

    private inline fun <reified T> putValue(key: String, value: T) {
        cache[key] = Json.encodeToString(value)
        persist()
    }

    override val keys: Set<String>
        get() = cache.keys
    override val size: Int
        get() = cache.size


    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = getValueOrDefault(key, defaultValue)

    override fun getBooleanOrNull(key: String): Boolean? = getValueOrNull(key)

    override fun getDouble(key: String, defaultValue: Double): Double = getValueOrDefault(key, defaultValue)

    override fun getDoubleOrNull(key: String): Double? = getValueOrNull(key)

    override fun getFloat(key: String, defaultValue: Float): Float = getValueOrDefault(key, defaultValue)

    override fun getFloatOrNull(key: String): Float? = getValueOrNull(key)

    override fun getInt(key: String, defaultValue: Int): Int = getValueOrDefault(key, defaultValue)

    override fun getIntOrNull(key: String): Int? = getValueOrNull(key)

    override fun getLong(key: String, defaultValue: Long): Long = getValueOrDefault(key, defaultValue)

    override fun getLongOrNull(key: String): Long? = getValueOrNull(key)

    override fun getString(key: String, defaultValue: String): String = getValueOrDefault(key, defaultValue)

    override fun getStringOrNull(key: String): String? = getValueOrNull(key)

    override fun hasKey(key: String): Boolean = cache.containsKey(key)

    override fun putBoolean(key: String, value: Boolean) = putValue(key, value)

    override fun putDouble(key: String, value: Double) = putValue(key, value)

    override fun putFloat(key: String, value: Float) = putValue(key, value)

    override fun putInt(key: String, value: Int) = putValue(key, value)

    override fun putLong(key: String, value: Long) = putValue(key, value)

    override fun putString(key: String, value: String) = putValue(key, value)

    override fun clear() {
        cache.clear()
        persist()
    }

    override fun remove(key: String) {
        cache.remove(key)
    }
}

