@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("dev.thomasharris.lemon.library")
}

android {
    namespace = "dev.thomasharris.lemon.core.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.lobstersApi)
    implementation(projects.core.database)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.app.cash.sqldelight.paging)
}
