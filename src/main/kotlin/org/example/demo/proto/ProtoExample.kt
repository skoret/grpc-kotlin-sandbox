package org.example.demo.proto

import com.google.protobuf.util.JsonFormat
import org.example.demo.proto.gen.ProtoEnum
import org.example.demo.proto.gen.ProtoMessage
import org.example.demo.proto.gen.NestedMessage

fun main() {
    val msg = `build message with kotlin apply`()

    val bytes = msg.toByteArray()
    val jsons = JsonFormat.printer()
        .omittingInsignificantWhitespace()
        .print(msg)

    println("bytes |  ${bytes.size} | [${bytes.joinToString(" ")}]")
    println("jsons | ${jsons.toByteArray().size} | $jsons")
}

fun `build message with kotlin apply`(): ProtoMessage = ProtoMessage.newBuilder().apply {
    intField = 13
    longField = 42
    stringField = "hi, protobuf"
    nestedField = NestedMessage.newBuilder().apply {
        addAllRepeatedField(listOf(ProtoEnum.ZERO, ProtoEnum.SECOND))
        putAllMapField(mapOf(1 to "a", 2 to "b"))
    }.build()
}.build()

fun `build message with java builder`(): ProtoMessage = ProtoMessage.newBuilder()
    .setIntField(13)
    .setLongField(42)
    .setStringField("hi, protobuf")
    .setNestedField(NestedMessage.newBuilder()
        .addAllRepeatedField(listOf(ProtoEnum.ZERO, ProtoEnum.SECOND))
        .putAllMapField(mapOf(1 to "a", 2 to "b"))
        .build())
    .build()