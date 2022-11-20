plugins {
    id("dev.thomasharris.lemon.library")
    id("dev.thomasharris.lemon.compose")
}

android {
    namespace = "dev.thomasharris.lemon.core.theme"
}

dependencies {
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
