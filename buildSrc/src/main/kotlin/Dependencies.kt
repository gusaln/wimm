/*
 * Copyright (c) 2023. Gustavo López. All rights reserved.
 */

object Versions {
    const val androidCompileSdkVersion = 33
    const val androidMinSdkVersion = 24
    const val androidTargetSdkVersion = 33


    const val sqlDelight = "1.5.4"
    const val koinCore = "3.3.2"
    const val koinAndroid = "3.3.2"
    const val koinAndroidCompose = "3.4.1"
}

object Dependencies {
    object Gradle {
        const val sqlDelight = "com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelight}"
    }

    object SqlDelight {
        const val runtime = "com.squareup.sqldelight:runtime:${Versions.sqlDelight}"
        const val coroutineExtensions = "com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelight}"
        const val androidDriver = "com.squareup.sqldelight:android-driver:${Versions.sqlDelight}"

        //        const val nativeDriver = "com.squareup.sqldelight:native-driver:${Versions.sqlDelight}"
        //const val nativeDriverMacos = "com.squareup.sqldelight:native-driver-macosx64:${Versions.sqlDelight}"
//        const val nativeDriverMacos = "com.squareup.sqldelight:native-driver-macosarm64:${Versions.sqlDelight}"
        const val sqliteDriver = "com.squareup.sqldelight:sqlite-driver:${Versions.sqlDelight}"
    }


    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koinCore}"
        const val test = "io.insert-koin:koin-test:${Versions.koinCore}"
        const val testJUnit4 = "io.insert-koin:koin-test-junit4:${Versions.koinCore}"
        const val android = "io.insert-koin:koin-android:${Versions.koinAndroid}"
        const val compose = "io.insert-koin:koin-androidx-compose:${Versions.koinAndroidCompose}"
    }
}