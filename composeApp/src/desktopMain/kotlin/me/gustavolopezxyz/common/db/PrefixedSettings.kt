/*
 * Copyright (c) 2024. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.db

import com.russhwolf.settings.Settings

class PrefixedSettings(private val delegate: Settings, private val prefix: String) : Settings {
    override val keys: Set<String>
        get() = delegate.keys.filter { it.startsWith("$prefix:") }.toSet()
    override val size: Int
        get() = keys.size

    override fun clear() {
        keys.forEach { delegate.remove(it) }
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate.getBoolean("$prefix:$key", defaultValue)

    override fun getBooleanOrNull(key: String): Boolean? = delegate.getBooleanOrNull("$prefix:$key")

    override fun getDouble(key: String, defaultValue: Double): Double = delegate.getDouble("$prefix:$key", defaultValue)

    override fun getDoubleOrNull(key: String): Double? = delegate.getDoubleOrNull("$prefix:$key")

    override fun getFloat(key: String, defaultValue: Float): Float = delegate.getFloat("$prefix:$key", defaultValue)

    override fun getFloatOrNull(key: String): Float? = delegate.getFloatOrNull("$prefix:$key")

    override fun getInt(key: String, defaultValue: Int): Int = delegate.getInt("$prefix:$key", defaultValue)

    override fun getIntOrNull(key: String): Int? = delegate.getIntOrNull("$prefix:$key")

    override fun getLong(key: String, defaultValue: Long): Long = delegate.getLong("$prefix:$key", defaultValue)

    override fun getLongOrNull(key: String): Long? = delegate.getLongOrNull("$prefix:$key")

    override fun getString(key: String, defaultValue: String): String = delegate.getString("$prefix:$key", defaultValue)

    override fun getStringOrNull(key: String): String? = delegate.getStringOrNull("$prefix:$key")

    override fun hasKey(key: String): Boolean = delegate.hasKey("$prefix:$key")

    override fun putBoolean(key: String, value: Boolean) = delegate.putBoolean("$prefix:$key", value)

    override fun putDouble(key: String, value: Double) = delegate.putDouble("$prefix:$key", value)

    override fun putFloat(key: String, value: Float) = delegate.putFloat("$prefix:$key", value)

    override fun putInt(key: String, value: Int) = delegate.putInt("$prefix:$key", value)

    override fun putLong(key: String, value: Long) = delegate.putLong("$prefix:$key", value)

    override fun putString(key: String, value: String) = delegate.putString("$prefix:$key", value)

    override fun remove(key: String) = delegate.remove("$prefix:$key")
}

fun Settings.prefix(prefix: String): Settings {
    return PrefixedSettings(this, prefix)
}