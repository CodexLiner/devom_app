import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinx.serialization)
    id("com.google.gms.google-services") version "4.4.3"
    id("com.google.firebase.crashlytics") version "3.0.5" apply true

}

kotlin {
    jvmToolchain(11)
    androidTarget {
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kermit)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.client.logging)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.composee)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.coil.network.ktor)
            implementation(libs.kotlinx.datetime)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.multiplatformSettings)
            implementation(libs.multiplatform.settings.no.arg)

            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            implementation(libs.filekit.core)
            implementation(libs.filekit.dialogs)
            implementation(libs.filekit.dialogs.compose)
            implementation(libs.filekit.coil)
            implementation("network.chaintech:cmpcharts:1.0.0")
            implementation("com.devom:platform:alpha.1.0.0")
            implementation("me.meenagopal24:razorpay:1.0.0")

        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.kotlinx.coroutines.test)
        }

        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.appcompat)
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.16.0"))
            implementation("com.google.firebase:firebase-messaging:23.4.0")

            implementation("com.google.android.gms:play-services-auth:21.0.0")
            implementation("com.google.firebase:firebase-auth:22.3.1")
            implementation("androidx.credentials:credentials:1.3.0")
            implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
            implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
            implementation("com.google.firebase:firebase-crashlytics-ndk")
            implementation("com.google.firebase:firebase-analytics")
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

    }
}

android {
    namespace = "com.devom.app"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        targetSdk = 36

        applicationId = "com.devom.app"
        versionCode = 3
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    signingConfigs {
        create("universal") {
            storeFile = file("DevomApp.jks")
            storePassword = "Devom@2025"
            keyAlias = "Devom"
            keyPassword = "Devom@2025"
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("universal")
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("universal")
        }
    }
}

//https://developer.android.com/develop/ui/compose/testing#setup
dependencies {
    androidTestImplementation(libs.androidx.uitest.junit4)
    debugImplementation(libs.androidx.uitest.testManifest)
}
