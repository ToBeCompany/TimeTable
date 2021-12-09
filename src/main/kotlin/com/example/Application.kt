package com.example

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.Duration

fun main() {
    val port = 8080 //Integer.parseInt(System.getenv("PORT"))

    embeddedServer(Netty, port = port) {
install(ContentNegotiation) {
    json()
}
        configureRouting()
        configureSerialization()
        configureSockets()
    }.start(wait = true)
}


