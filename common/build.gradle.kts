plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("app.cash.sqldelight")
}

group = "me.gustavolopezxyz"
version = "1.0-SNAPSHOT"

kotlin {
    androidTarget()

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
                api(compose.material3)
                api(compose.materialIconsExtended)
                api(projectLibs.koin.core)
                api(projectLibs.koin.test)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(compose.preview)

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
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(projectLibs.android.junit)
                implementation(projectLibs.sqldelight.androidDriver)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation(compose.desktop.common)
                implementation(projectLibs.sqldelight.sqliteDriver)
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "me.gustavolopezxyz.wimm.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(17)
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("me.gustavolopezxyz.common.data")
            schemaOutputDirectory.set(file("me.gustavolopezxyz.common.data"))
            migrationOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            deriveSchemaFromMigrations.set(true)
            verifyMigrations.set(true)

            dialect(projectLibs.sqldelight.dialects.sqlite)
        }
    }
}