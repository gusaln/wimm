import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)

    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm("desktop") {
        group = BuildConstants.NameSpaces.group
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.sqldelight.androidDriver)
        }
        androidNativeTest.dependencies {
            implementation(libs.androidx.test.junit)
            implementation(libs.sqldelight.androidDriver)

            implementation(libs.koin.test)
            implementation(libs.koin.testJUnit4)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.sqldelight.sqliteDriver)
        }


        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(compose.preview)

            api(libs.koin.core)
            api(libs.koin.test)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutineExtensions)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.koin.test)
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = BuildConstants.NameSpaces.Android.app
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = BuildConstants.AndroidApp.id
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = BuildConstants.AndroidApp.versionCode
        versionName = BuildConstants.AndroidApp.versionName
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Deb)
            packageName = BuildConstants.DesktopApp.packageName
            packageVersion = BuildConstants.DesktopApp.packageVersion
        }
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

            dialect(libs.sqldelight.dialects.sqlite)
        }
    }
}