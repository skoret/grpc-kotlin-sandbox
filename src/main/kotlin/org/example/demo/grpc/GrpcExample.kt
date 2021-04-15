package org.example.demo.grpc

import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import org.example.demo.grpc.gen.GreetRequest
import org.example.demo.grpc.gen.GreetResponse
import org.example.demo.grpc.gen.GreeterGrpc
import org.example.demo.grpc.gen.GreeterGrpcKt

private class GreeterServiceJava : GreeterGrpc.GreeterImplBase() {
    override fun greet(request: GreetRequest?, responseObserver: StreamObserver<GreetResponse>?) {
        requireNotNull(request)
        requireNotNull(responseObserver)

        responseObserver.onNext(GreetResponse.newBuilder().setMessage("hello, ${request.name}").build())
        responseObserver.onCompleted()
    }

    override fun greetAnyone(responseObserver: StreamObserver<GreetResponse>?): StreamObserver<GreetRequest> {
        requireNotNull(responseObserver)

        return object : StreamObserver<GreetRequest> {
            private val names = mutableListOf<String>()
            override fun onNext(value: GreetRequest?) {
                value?.let { names.add(it.name) }
            }

            override fun onError(t: Throwable?) {}

            override fun onCompleted() {
                responseObserver.onNext(GreetResponse.newBuilder().setMessage("hello, ${names.random()}").build())
                responseObserver.onCompleted()
            }
        }
    }

    override fun greetFromAll(request: GreetRequest?, responseObserver: StreamObserver<GreetResponse>?) {
        requireNotNull(request)
        requireNotNull(responseObserver)

        listOf("hello", "hola", "shalom").forEach { greet ->
            responseObserver.onNext(GreetResponse.newBuilder().setMessage("$greet, ${request.name}").build())
        }
        responseObserver.onCompleted()
    }

    override fun greetEveryone(responseObserver: StreamObserver<GreetResponse>?): StreamObserver<GreetRequest> {
        requireNotNull(responseObserver)

        return object : StreamObserver<GreetRequest> {
            private val greetings = listOf("hello", "hola", "shalom")
            override fun onNext(value: GreetRequest?) {
                value?.let {
                    responseObserver.onNext(GreetResponse.newBuilder().setMessage("${greetings.random()}, ${it.name}").build())
                }
            }

            override fun onError(t: Throwable?) {}

            override fun onCompleted() {
                responseObserver.onCompleted()
            }
        }
    }
}

private val channel = ManagedChannelBuilder.forTarget("localhost:50051").usePlaintext().build()
private val asyncStub = GreeterGrpc.newStub(channel)
private val blockingStub = GreeterGrpc.newBlockingStub(channel)
private val futureStub = GreeterGrpc.newFutureStub(channel)
private val coroutineStub = GreeterGrpcKt.GreeterCoroutineStub(channel)

