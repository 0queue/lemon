@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.thomasharris.lemon.application")
    id("dev.thomasharris.lemon.compose")
    id("dev.thomasharris.lemon.hilt")
}

android {
    namespace = "dev.thomasharris.lemon"

    defaultConfig {
        applicationId = "dev.thomasharris.lemon"
        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }

        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            signingConfig = buildTypes.getByName("debug").signingConfig
        }
    }
}

dependencies {
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation)

    implementation(projects.core.lobstersApi)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.theme)
    implementation(projects.core.ui)
    implementation(projects.feature.comments)
    implementation(projects.feature.frontpage)
    implementation(projects.feature.settings)
    implementation(projects.feature.userprofile)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)

    implementation(libs.kotlin.result)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    implementation(libs.coil)
}
