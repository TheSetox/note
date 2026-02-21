plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
}

apply(from = rootProject.file("gradle/spotless-module.gradle.kts"))

kotlin {
    jvmToolchain(17)
    androidLibrary {
        namespace = "com.example.notes.core.ui"
        compileSdk =
            libs.versions.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.minSdk
                .get()
                .toInt()
        withJava()
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }
    }
    jvm("desktop")
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
