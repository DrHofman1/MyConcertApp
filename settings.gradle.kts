pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        // Itt adhatod meg a plugin(ok) verzióit,
        // ha szeretnéd (opcionális).
        // Például:
         id("com.android.application") version "8.1.0"
        id("org.jetbrains.kotlin.android") version "1.8.10"
        id("com.google.gms.google-services") version "4.3.15"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyConcertApp"
include(":app")


