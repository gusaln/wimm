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
                api(Dependencies.Koin.core)
                api(Dependencies.Koin.test)

                implementation(Dependencies.Navigation.core)
                implementation(Dependencies.SqlDelight.runtime)
                implementation(Dependencies.SqlDelight.coroutineExtensions)
                implementation(Dependencies.KotlinX.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Dependencies.Koin.test)
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
                implementation(Dependencies.SqlDelight.androidDriver)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation(Dependencies.SqlDelight.androidDriver)
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation(Dependencies.SqlDelight.sqliteDriver)
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
        targetSdkVersion(Versions.androidTargetSdkVersion)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

sqldelight {
    database("Database") {
        packageName = "me.gustavolopezxyz.db"
//        sourceFolders = listOf("sqldelight")
    }
}