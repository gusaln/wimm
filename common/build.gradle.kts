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
                api(deps.koin.core)
                api(deps.koin.test)

                implementation(deps.sqldelight.runtime)
                implementation(deps.sqldelight.coroutineExtensions)
                implementation(deps.kotlinx.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(deps.koin.test)
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api(deps.android.appcompat)
                api(deps.android.coreKtx)
                implementation(deps.sqldelight.androidDriver)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(deps.android.junit)
                implementation(deps.sqldelight.androidDriver)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation(deps.sqldelight.sqliteDriver)
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
        packageName = "me.gustavolopezxyz.db"
        schemaOutputDirectory = file("me.gustavolopezxyz.db")
        migrationOutputDirectory = file("src/commonMain/sqldelight/databases")
        deriveSchemaFromMigrations = true
        verifyMigrations = true
    }
}