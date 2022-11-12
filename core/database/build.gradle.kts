@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("dev.thomasharris.lemon.library")
    id("dev.thomasharris.lemon.hilt")
    alias(libs.plugins.app.cash.sqldelight)
}

android {
    namespace = "dev.thomasharris.lemon.core.database"
}

dependencies {
    implementation(libs.kotlinx.datetime)

    implementation(libs.app.cash.sqldelight.android.driver)
    api(libs.app.cash.sqldelight.coroutines)
}

sqldelight {
    database("LobstersDatabase") {
        packageName = "dev.thomasharris.lemon.core.database"
    }
}
