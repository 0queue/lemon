@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.org.jetbrains.kotlin.jvm)
}

dependencies {
    implementation(libs.com.google.errorprone.annotations)
}