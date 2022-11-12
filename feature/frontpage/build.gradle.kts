@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("dev.thomasharris.lemon.feature")
}

android {
    namespace = "dev.thomasharris.lemon.feature.frontpage"
}

dependencies {
    implementation(projects.lemon.core.data)
    // TODO remove this once I map to the actual model
    implementation(projects.lemon.core.database)
    implementation(projects.lemon.core.model)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
}
