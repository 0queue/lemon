@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.contentnegotiation)
    implementation(libs.ktor.kotlinx.serialization)

    implementation(libs.kotlin.result)
    implementation(libs.kotlin.result.coroutines)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    implementation(projects.core.model)
}
