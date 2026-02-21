import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.withGroovyBuilder

pluginManager.apply("com.diffplug.spotless")

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion

extensions.getByName("spotless").withGroovyBuilder {
    "kotlin" {
        "target"("**/*.kt")
        "targetExclude"("**/build/**/*.kt")
        "ktlint"(ktlintVersion)
    }
    "kotlinGradle" {
        "target"("*.gradle.kts")
        "ktlint"(ktlintVersion)
    }
}
