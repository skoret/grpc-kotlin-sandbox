pluginManagement {
    val `kotlin-version`: String by settings
    val `protobuf-plugin`: String by settings

    plugins {
        kotlin("jvm") version `kotlin-version`
        id("com.google.protobuf") version `protobuf-plugin`
    }
}

rootProject.name = "grpc-kotlin-sandbox"
