plugins {
    alias(libs.plugins.kotlin.jvm)
}

apply(from = rootProject.file("gradle/spotless-module.gradle.kts"))

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.detekt.api)
    testImplementation(kotlin("test"))
    testImplementation(libs.detekt.test)
}
