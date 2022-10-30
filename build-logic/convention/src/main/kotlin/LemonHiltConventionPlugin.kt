import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class LemonHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        pluginManager.run {
            apply("org.jetbrains.kotlin.kapt")
            apply("com.google.dagger.hilt.android")
        }

        extensions.configure<KaptExtension> {
            correctErrorTypes = true
        }

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        val hiltDependency =
            libs.findLibrary("com-google-dagger-hilt-android").get()
        val hiltCompilerDependency =
            libs.findLibrary("com-google-dagger-hilt-android-compiler").get()

        dependencies.add("implementation", hiltDependency)
        dependencies.add("kapt", hiltCompilerDependency)
    }
}
