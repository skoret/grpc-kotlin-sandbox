package org.example.demo.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.example.demo.grpc.gen.GreetRequest
import org.example.demo.grpc.gen.GreetResponse
import org.example.demo.grpc.gen.GreeterGrpcKt.GreeterCoroutineImplBase
import kotlin.random.Random

private class GreeterService : GreeterCoroutineImplBase() {
    private val rng = Random(42)
    private val greetings = listOf(
        "hello",
        "bonjour",
        "hola",
        "shalom",
        "nǐ hǎo",
    )

    override suspend fun greet(request: GreetRequest): GreetResponse {
        println("receive request for greet( ${request.name} )")

        println("------------")
        return GreetResponse.newBuilder().apply {
            message = "${greetings.random(rng)}, ${request.name}!"
        }.build()
    }

    override suspend fun greetAnyone(requests: Flow<GreetRequest>): GreetResponse {
        println("receive stream of request for greetAnyone(...)")

        val name = requests.withIndex().onEach { delay(1000) }
            .onEach { println("process request #${it.index} for '${it.value.name}'") }
            .map { it.value.name }
            .onCompletion { println("------------") }
            .toList()
            .random(rng)

        return GreetResponse.newBuilder().apply {
            message = "${greetings.random(rng)}, $name!"
        }.build()
    }

    override fun greetFromAll(request: GreetRequest): Flow<GreetResponse> {
        println("receive request for greetFromAll( ${request.name} )")

        println("------------")
        return greetings.asFlow().onEach { delay(1000) }
            .onEach { greeting -> println("map '$greeting' to response") }
            .map { greeting ->
                GreetResponse.newBuilder().apply {
                    message = "${greeting}, ${request.name}!"
                }.build()
            }
            .onCompletion { println("------------") }
    }

    override fun greetEveryone(requests: Flow<GreetRequest>): Flow<GreetResponse> {
        println("receive stream of request for greetEveryone(...)")

        return requests.withIndex().onEach { delay(1000) }
            .onEach { println("process request #${it.index} for '${it.value.name}'") }
            .map {
                GreetResponse.newBuilder().apply {
                    message = "${greetings.random(rng)}, ${it.value.name}!"
                }.build()
            }
            .onCompletion { println("------------") }
    }
}

private class GreeterServer(private val port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(GreeterService())
        .build()

    fun start() {
        server.start()
        println("Server started, listening on $port")
        Runtime.getRuntime().addShutdownHook(
            Thread {
                println("*** shutting down gRPC server since JVM is shutting down")
                stop()
                println("*** server shut down")
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun await() {
        server.awaitTermination()
    }
}

fun main() {
    val port = 50051
    val server = GreeterServer(port)
    server.start()
    server.await()
}