pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("android").version(extra["kotlin.version"] as String)
        id("com.android.application").version(extra["agp.version"] as String)
        id("com.android.library").version(extra["agp.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }

    dependencyResolutionManagement {
        versionCatalogs {
            val sqlDelight = "1.5.4"
            val navigation = "2.5.3"
            val koinCore = "3.3.2"
            val koinAndroid = "3.3.2"
            val koinAndroidCompose = "3.4.1"
            val kotlinXDatetime = "0.4.0"
            val appCompat = "1.6.0"
            val androidCoreKtx = "1.9.0"
            val androidActivityCompose = "1.6.1"
            val androidJUnit = "4.13.2"

            create("gradlePlugins") {
                library("sqldelight.plugin", "com.squareup.sqldelight:gradle-plugin:${sqlDelight}")

            }

            create("deps") {
//                library("navigation.core", "androidx.navigation:navigation-compose:${navigation}")
                library("sqldelight.runtime", "com.squareup.sqldelight:runtime:${sqlDelight}")
                library(
                    "sqldelight.coroutineExtensions",
                    "com.squareup.sqldelight:coroutines-extensions:${sqlDelight}"
                )
                library("sqldelight.androidDriver", "com.squareup.sqldelight:android-driver:${sqlDelight}")
                library("sqldelight.sqliteDriver", "com.squareup.sqldelight:sqlite-driver:${sqlDelight}")

                library("koin.core", "io.insert-koin:koin-core:${koinCore}")
                library("koin.test", "io.insert-koin:koin-test:${koinCore}")
                library("koin.testJUnit4", "io.insert-koin:koin-test-junit4:${koinCore}")
                library("koin.android", "io.insert-koin:koin-android:${koinAndroid}")
                library("koin.compose", "io.insert-koin:koin-androidx-compose:${koinAndroidCompose}")

                library("kotlinx.datetime", "org.jetbrains.kotlinx:kotlinx-datetime:${kotlinXDatetime}")

                library("android-appcompat", "androidx.appcompat:appcompat:$appCompat")
                library("android-coreKtx", "androidx.core:core-ktx:$androidCoreKtx")
                library("android-activityCompose", "androidx.activity:activity-compose:$androidActivityCompose")
                library("android-junit", "junit:junit:$androidJUnit")
            }
        }
    }
}

rootProject.name = "wimm"

include(":android", ":desktop", ":common")
