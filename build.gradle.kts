import com.google.protobuf.gradle.*
import org.gradle.kotlin.dsl.proto
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    kotlin("jvm")
    id("com.google.protobuf")
}

group = "org.example.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
val `kotlin-coroutines`: String by project
val `protobuf-version`: String by project
val `grpc-java`: String by project
val `grpc-kotlin`: String by project
val `grpc-annotations`: String by project

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$`kotlin-coroutines`")

    implementation("io.grpc:grpc-kotlin-stub:$`grpc-kotlin`")
    implementation("com.google.protobuf:protobuf-java-util:$`protobuf-version`")
    implementation("org.apache.tomcat:annotations-api:$`grpc-annotations`") // necessary for grpc with Java 9+
    runtimeOnly("io.grpc:grpc-netty-shaded:$`grpc-java`")
}

// protobuf compiler + grpc plugin configuration
protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$`protobuf-version`"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$`grpc-java`"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$`grpc-kotlin`:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

// protobuf/grpc source files path configuration
sourceSets {
    main {
        proto {
            srcDir("src/main/proto")
        }
        java {
            srcDirs(
                "build/generated/source/proto/main/grpc",
                "build/generated/source/proto/main/java"
            )
        }
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDirs("build/generated/source/proto/main/grpckt")
        }
    }
}

// run './gradlew --console=plain --quiet client'
tasks.register("client", JavaExec::class) {
    dependsOn("classes")
    main = "org.example.demo.grpc.GrpcClientKt"
    classpath = sourceSets.main.get().runtimeClasspath
    standardInput = System.`in`
}

// run './gradlew --console=plain --quiet server'
tasks.register("server", JavaExec::class) {
    dependsOn("classes")
    main = "org.example.demo.grpc.GrpcServerKt"
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}