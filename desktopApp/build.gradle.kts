plugins {
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.hot.reload)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.jvm)
    application
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.example.notes.desktop.MainKt")
}

dependencies {
    implementation(projects.core.ui)
    implementation(projects.feature.notes)
    implementation(compose.desktop.currentOs)
}
