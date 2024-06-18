/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

package me.gustavolopezxyz.common.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.cache.storage.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import me.gustavolopezxyz.common.db.DatabaseFactory
import me.gustavolopezxyz.common.db.FileSettings
import me.gustavolopezxyz.desktop.Config
import me.gustavolopezxyz.desktop.ConfigFactory
import me.gustavolopezxyz.desktop.services.BackupService
import me.gustavolopezxyz.desktop.services.SnackbarService
import org.kodein.di.*
import java.nio.file.Files
import java.nio.file.Paths

actual fun platformModule(): DI.Module {
    return DI.Module(name = "PLATFORM") {
        bindInstance {
            ConfigFactory.new().apply {
                ConfigFactory.logger.info("App dir: $dataDir")
                ConfigFactory.logger.info("Data file: $dataFilePath")
            }
        }

        bindSingleton {
            BackupService(instance())
        }

        bindSingleton {
            DatabaseFactory(instance()).create()
        }

        bindSingleton {
            FileSettings(config = instance())
        }

        bindSingleton {
            SnackbarService()
        }

        bindProvider {
            HttpClient(CIO) {
                install(Logging)
                install(ContentNegotiation) {
                    json()
                }
                install(HttpCache) {
                    val cacheFile =
                        Files.createDirectories(Paths.get(instance<Config>().dataDir, "cache", "http")).toFile()
                    publicStorage(FileStorage(cacheFile))
                }
            }
        }
    }
}
