pluginManagement {
    repositories {
        // Where Gradle should look for plugins
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        // Pin plugin versions here so modules can apply them without versions
        id("com.android.application") version "8.5.2"
        id("org.jetbrains.kotlin.android") version "1.9.24"
        id("org.jetbrains.kotlin.kapt") version "1.9.24"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Where your dependencies come from
        google()
        mavenCentral()
    }
}

rootProject.name = "SpiceStyle2"
include(":app")
