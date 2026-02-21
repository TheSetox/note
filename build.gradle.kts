import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.hot.reload) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt) apply false
}

allprojects {
    group = "com.example.notes"
    version = "0.1.0"
}

val ktlintVersion = libs.versions.ktlint.get()

spotless {
    kotlinGradle {
        target("*.gradle.kts", "gradle/**/*.kts")
        ktlint(ktlintVersion)
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")

    if (name != "iosApp") {
        apply(plugin = "io.gitlab.arturbosch.detekt")

        extensions.configure<DetektExtension> {
            buildUponDefaultConfig = true
            allRules = false
            autoCorrect = false
            ignoreFailures = false
        }

        tasks.withType<Detekt>().configureEach {
            setSource(files(projectDir.resolve("src")))
            include("**/*.kt", "**/*.kts")
            exclude("**/build/**")

            reports {
                html.required.set(true)
                xml.required.set(true)
                sarif.required.set(true)
                md.required.set(false)
            }
        }
    }
}
