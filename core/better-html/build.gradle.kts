@file:Suppress("UnstableApiUsage")

plugins {
    id("dev.thomasharris.lemon.library")
    id("dev.thomasharris.lemon.compose")
}

android {
    namespace = "dev.thomasharris.lemon.core.betterhtml"

    buildFeatures {
        this.viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core)
    implementation(libs.jsoup)
//    implementation(libs.coil)

//    implementation(libs.kotlinx.coroutines.android)

//    implementation(projects.core.model)
//    implementation(libs.kotlinx.datetime)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(projects.core.theme)
}
