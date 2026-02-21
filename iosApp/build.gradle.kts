plugins {
    base
}

apply(from = rootProject.file("gradle/spotless-module.gradle.kts"))

tasks.register<Exec>("buildIosSimulatorApp") {
    workingDir = projectDir
    commandLine(
        "xcodebuild",
        "-project",
        "iosApp.xcodeproj",
        "-scheme",
        "iosApp",
        "-destination",
        "generic/platform=iOS Simulator",
        "build",
    )
}
