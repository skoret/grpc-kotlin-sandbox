package org.example.demo.grpc

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.example.demo.grpc.gen.GreetRequest
import org.example.demo.grpc.gen.GreeterGrpcKt.GreeterCoroutineStub
import java.io.Closeable
import java.util.concurrent.TimeUnit

class GreeterClient(address: String) : Closeable {
    private val channel = ManagedChannelBuilder
        .forTarget(address)
        .usePlaintext()
        .executor(Dispatchers.Default.asExecutor())
        .build()

    private val stub: GreeterCoroutineStub = GreeterCoroutineStub(channel)

    suspend fun greet(arg: String) {
        println("------------")
        val request = GreetRequest.newBuilder().apply { name = arg }.build()

        println("call greet( $arg )")
        val response = stub.greet(request)

        println("result: ${response.message}")
        println("------------")
    }

    suspend fun greetAnyone(vararg args: String) {
        println("------------")
        val requests = args.asFlow()
            .onEach { arg -> println("send '$arg' to request"); delay(1000) }
            .map { arg -> GreetRequest.newBuilder().apply { name = arg }.build() }

        println("call greetSomeone( ${args.joinToString()} )")
        val response = stub.greetAnyone(requests)

        println("result: ${response.message}")
        println("------------")
    }

    suspend fun greetFromAll(arg: String) {
        println("------------")
        val request = GreetRequest.newBuilder().apply { name = arg }.build()

        println("call greetFromAll( $arg )")
        val responses = stub.greetFromAll(request)

        println("start collecting responses of greetFromAll")
        responses.collectIndexed { i, response ->
            println("result #$i: ${response.message}"); delay(1000)
        }
        println("------------")
    }

    suspend fun greetEveryone(vararg args: String) {
        println("------------")

        val requests = args.asFlow()
            .onEach { arg -> println("map '$arg' to request"); delay(1000) }
            .map { arg -> GreetRequest.newBuilder().apply { name = arg }.build() }

        println("call greetEveryone( ${args.joinToString()} )")
        val responses = stub.greetEveryone(requests)

        println("start collecting responses of greetEveryone")
        responses.collectIndexed { i, response ->
            println("result #$i: ${response.message}"); delay(1000)
        }
        println("------------")
    }

    override fun close() {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS)
    }
}

fun main() = runBlocking {
    GreeterClient("localhost:50051").use { client ->
        pause()
        client.greet("world")

        pause()
        client.greetAnyone("alice", "bob", "carol")

        pause()
        client.greetFromAll("world")

        pause()
        client.greetEveryone("alice", "bob", "carol")
    }
}

fun pause() {
    println("type any key...")
    readLine()
}