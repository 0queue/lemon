plugins {
    id("dev.thomasharris.lemon.library")
    id("dev.thomasharris.lemon.hilt")
}

android {
    namespace = "dev.thomasharris.lemon.core.data"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.lobstersApi)
    implementation(projects.core.database)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlin.result)

    implementation(libs.androidx.paging.runtime)
    implementation(libs.app.cash.sqldelight.paging)
}
