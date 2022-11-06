plugins {
    `kotlin-dsl`
}

kotlinDslPluginOptions {
    jvmTarget.set("11")
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "dev.thomasharris.lemon.application"
            implementationClass = "LemonApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "dev.thomasharris.lemon.library"
            implementationClass = "LemonLibraryConventionPlugin"
        }

        register("compose") {
            id = "dev.thomasharris.lemon.compose"
            implementationClass = "LemonComposeConventionPlugin"
        }

        register("hilt") {
            id = "dev.thomasharris.lemon.hilt"
            implementationClass = "LemonHiltConventionPlugin"
        }

        register("feature") {
            id = "dev.thomasharris.lemon.feature"
            implementationClass = "LemonFeatureConventionPlugin"
        }
    }
}

dependencies {
    implementation(libs.gradlePlugin.android)
    implementation(libs.gradlePlugin.kotlin)
}
