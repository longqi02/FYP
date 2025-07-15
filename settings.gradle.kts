pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // Declare the google-services plugin and its version here
        id("com.google.gms.google-services") version "4.3.15"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        // ← Add this so Gradle can pull PyTorch Android artifacts
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/pytorch/")
        }
    }
}

rootProject.name = "Traffic Sign APP"
include(":app")
 