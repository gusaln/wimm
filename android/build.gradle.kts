plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

group = "me.gustavolopezxyz"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation("androidx.activity:activity-compose:1.5.0")

    with(Dependencies.Koin) {
        implementation(core)
        implementation(android)
        implementation(compose)
        testImplementation(test)
        testImplementation(testJUnit4)
    }

    implementation(project(":common"))
}

android {
    compileSdk = Versions.androidCompileSdkVersion
    defaultConfig {
        minSdk = Versions.androidMinSdkVersion
        targetSdk = Versions.androidTargetSdkVersion

        applicationId = "me.gustavolopezxyz.wimm.android"
        versionCode = 1
        versionName = "1.0-SNAPSHOT"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}