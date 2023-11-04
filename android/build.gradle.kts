plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.application")
}

repositories {
    mavenCentral()
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(projectLibs.android.activityCompose)

                with(projectLibs.koin) {
                    implementation(core)
                    implementation(android)
                    implementation(compose)
                }

                implementation(project(":common"))
            }
        }

        val androidInstrumentedTest by getting {
            dependencies {
                implementation(projectLibs.koin.test)
                implementation(projectLibs.koin.testJUnit4)
            }
        }
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = BuildConstants.NameSpaces.Android.app

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = BuildConstants.AndroidApp.id
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = BuildConstants.AndroidApp.versionCode
        versionName = BuildConstants.AndroidApp.versionName
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(17)
    }
}