import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import dev.thomasharris.lemon.buildlogic.configureCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// Trying something out, where compose plugin can be applied
// after either application or library projects, instead of
// creating two compose plugins, one for each
class LemonComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        pluginManager.run {
            withPlugin("com.android.library") {
                extensions.configure<LibraryExtension> {
                    configureCompose(this)
                }
            }

            withPlugin("com.android.application") {
                extensions.configure<ApplicationExtension> {
                    configureCompose(this)
                }
            }

            apply("org.jetbrains.kotlin.plugin.compose")
        }
    }
}
