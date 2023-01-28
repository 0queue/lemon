plugins {
    id("dev.thomasharris.lemon.feature")
}

android {
    namespace = "dev.thomasharris.lemon.feature.frontpage"
}

dependencies {
    implementation(projects.lemon.core.betterHtml)
    implementation(projects.lemon.core.data)
    implementation(projects.lemon.core.model)
    implementation(projects.lemon.core.ui)

    implementation(libs.androidx.compose.material2)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}
