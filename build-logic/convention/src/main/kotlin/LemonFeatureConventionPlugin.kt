import dev.thomasharris.lemon.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class LemonFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        pluginManager.run {
            apply("dev.thomasharris.lemon.library")
            apply("dev.thomasharris.lemon.compose")
            apply("dev.thomasharris.lemon.hilt")
        }

        dependencies {
//            add("implementation", libs.findLibrary("androidx.activity").get())
            add("implementation", libs.findLibrary("androidx.compose.material3").get())
            add("implementation", libs.findLibrary("androidx.compose.ui").get())
            add("implementation", libs.findLibrary("androidx.compose.ui.tooling.preview").get())
            add("implementation", libs.findLibrary("androidx.core").get())
            add("implementation", libs.findLibrary("androidx.lifecycle").get())
            add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel").get())
            add("implementation", libs.findLibrary("androidx.lifecycle.viewmodel.compose").get())

            add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())

            add("debugImplementation", libs.findLibrary("androidx.compose.ui.tooling").get())
            add("debugImplementation", libs.findLibrary("androidx.compose.ui.test.manifest").get())

            add("implementation", libs.findLibrary("androidx.navigation").get())
            add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
        }
    }
}
