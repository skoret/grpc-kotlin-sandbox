# grpc-kotlin-sandbox
Sample project with grpc client and server example using kotlin

## Setup
- Kotlin `1.4.32`
- Coroutines `1.4.3`
- Gradle `6.8.3`
- Protobuf Gradle Plugin `0.8.15`
- Protobuf Compiler `3.15.8`
- gRPC Java `1.37.0`
- gRPC Kotlin `1.0.0`

For build configuration and all third-party dependencies versions see [build.gradle.kts](./build.gradle.kts) and [gradle.properties](./gradle.properties)

## Build & Run
Instead of `gradle` use `./gradlew` or `gradlew.bat` on Windows  
```sh
gradle generateProto # to generate java classes, services and client stubs
```
```sh
gradle build # to build entire project 
```
```sh
gradle --console=plain --quiet server # to run server or run `main` method in `GrpcServer.kt` in IDE
```
```sh
gradle --console=plain --quiet client # to run client or run `main` method in `GrpcClient.kt` in IDE
```
```bash
# on each console prompt where client is running
# type `↵ Enter/Return` to fire requests from client to server
> 
```

## Materials
- [Presentation for SE students (RU)](./presentation_ru.pdf)
  - [Google Slides](https://docs.google.com/presentation/d/1hRQUnmraT8Yq9wWdgNZXiRsj96T1WmaGzQKATBsuBwg)
- [Protocol Buffers | Google Developers](https://developers.google.com/protocol-buffers)
  - [github.com/protocolbuffers/protobuf](https://github.com/protocolbuffers/protobuf)
  - [kotlin support request #3742](https://github.com/protocolbuffers/protobuf/issues/3742)
- [Kotlin | gRPC](https://grpc.io/docs/languages/kotlin/)
  - [github.com/grpc/grpc-kotlin](https://github.com/grpc/grpc-kotlin)
- [github.com/marcoferrer/kroto-plus](https://github.com/marcoferrer/kroto-plus)
- [github.com/streem/pbandk](https://github.com/streem/pbandk)
