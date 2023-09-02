import dev.thomasharris.lemon.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class LemonHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        pluginManager.run {
            apply("com.google.devtools.ksp")
            apply("com.google.dagger.hilt.android")
        }

        val hiltDependency =
            libs.findLibrary("com-google-dagger-hilt-android").get()
        val hiltCompilerDependency =
            libs.findLibrary("com-google-dagger-hilt-android-compiler").get()

        dependencies.add("implementation", hiltDependency)
        dependencies.add("ksp", hiltCompilerDependency)
    }
}
