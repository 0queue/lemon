plugins {
    id("dev.thomasharris.lemon.feature")
}

android {
    namespace = "dev.thomasharris.lemon.feature.comments"
}

dependencies {
    implementation(projects.lemon.core.data)
    implementation(projects.lemon.core.model)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}