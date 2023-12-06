/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common

data class Version(val mayor: UShort = 0u, val minor: UShort = 0u, val patch: UShort = 0u, val flag: String? = null) {
    constructor(mayor: Number = 0, minor: Number = 0, patch: Number = 0, flag: String? = null) : this(
        mayor.toShort().toUShort(), minor.toShort().toUShort(), patch.toShort().toUShort(), flag
    )

    operator fun compareTo(other: Version): Int {
        if (mayor.compareTo(other.mayor) != 0) {
            return mayor.compareTo(other.mayor)
        }

        if (minor.compareTo(other.minor) != 0) {
            return minor.compareTo(other.minor)
        }

        if (patch.compareTo(other.patch) != 0) {
            return patch.compareTo(other.patch)
        }

        return flag?.compareTo(other.flag ?: "") ?: 0
    }

    override fun toString() = if (flag == null) "$mayor.$minor.$patch" else "$mayor.$minor.$patch-$flag"
}