/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.logging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object Logging {
    fun of(name: String): Logger = LoggerFactory.getLogger(name)

    fun of(ofClass: Class<*>): Logger = of(ofClass.name)
}

@Composable
fun rememberLogger(name: String): Logger = remember { Logging.of(name) }

/** Lazily create a logger from the current class */
inline fun <reified T : Any> T.logger(): Lazy<Logger> = lazy { Logging.of(T::class.java) }

//inline val <reified T : Any> T.logger: Logger
//    get() = Logging.of(T::class.java)
