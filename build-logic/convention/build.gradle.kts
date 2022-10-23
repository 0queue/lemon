plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "dev.thomasharris.lemon.application"
            implementationClass = "LemonApplicationConventionPlugin"
        }
    }
}

dependencies {
    implementation(libs.gradlePlugin.android)
    implementation(libs.gradlePlugin.kotlin)
}
