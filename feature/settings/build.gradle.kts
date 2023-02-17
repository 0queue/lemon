plugins {
    id("dev.thomasharris.lemon.feature")
}

android {
    namespace = "dev.thomasharris.lemon.feature.settings"
}

dependencies {
    implementation(projects.lemon.core.ui)
    implementation(projects.lemon.core.theme)

    implementation(libs.kotlin.result)
}
