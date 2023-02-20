import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class LemonKotlinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        pluginManager.run {
            apply("org.jetbrains.kotlin.jvm")
        }

        extensions.configure<KotlinProjectExtension> {
            jvmToolchain(17)
        }

        // TODO maybe also import kotlin-result
    }
}
