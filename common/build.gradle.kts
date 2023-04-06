plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

group = "me.gustavolopezxyz"
version = "1.0-SNAPSHOT"

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(projectLibs.koin.core)
                api(projectLibs.koin.test)

                implementation(projectLibs.sqldelight.runtime)
                implementation(projectLibs.sqldelight.coroutineExtensions)
                implementation(projectLibs.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projectLibs.koin.test)
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api(projectLibs.android.appcompat)
                api(projectLibs.android.coreKtx)
                implementation(projectLibs.sqldelight.androidDriver)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(projectLibs.android.junit)
                implementation(projectLibs.sqldelight.androidDriver)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation(projectLibs.sqldelight.sqliteDriver)
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = Versions.androidCompileSdkVersion
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Versions.androidMinSdkVersion
        targetSdk = Versions.androidTargetSdkVersion
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    database("Database") {
        packageName = "me.gustavolopezxyz.common.data"
        schemaOutputDirectory = file("me.gustavolopezxyz.common.data")
        migrationOutputDirectory = file("src/commonMain/sqldelight/databases")
        deriveSchemaFromMigrations = true
        verifyMigrations = true
    }
}