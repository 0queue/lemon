@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("dev.thomasharris.lemon.library")
    id("dev.thomasharris.lemon.hilt")
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
}

android {
    namespace = "dev.thomasharris.lemon.core.datastore"
}

dependencies {
    // meh
    api(libs.androidx.datastore)

    implementation(libs.kotlinx.serialization.json)
}
