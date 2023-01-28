plugins {
    id("dev.thomasharris.lemon.feature")
}

android {
    namespace = "dev.thomasharris.lemon.feature.comments"
}

dependencies {
    implementation(projects.lemon.core.betterHtml)
    implementation(projects.lemon.core.data)
    implementation(projects.lemon.core.model)
    implementation(projects.lemon.core.ui)

    implementation(libs.coil)

    implementation(libs.kotlinx.datetime)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.compose.material2)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}
