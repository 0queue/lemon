plugins {
    id("dev.thomasharris.lemon.library")
    id("dev.thomasharris.lemon.compose")
}

// TODO this library will probably end up just being a kotlin library, just testing for now

android {
    namespace = "dev.thomasharris.lemon.lobstersapi"
}

dependencies {
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
