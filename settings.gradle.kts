pluginManagement {
    val `kotlin-version`: String by settings
    plugins {
        kotlin("jvm") version `kotlin-version`
    }
}

rootProject.name = "grpc-kotlin-sandbox"

