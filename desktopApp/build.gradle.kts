plugins {
    alias(libs.plugins.compose.multiplatform)
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
    implementation(project(":core:ui"))
    implementation(project(":feature:notes"))
    implementation(compose.desktop.currentOs)
}
