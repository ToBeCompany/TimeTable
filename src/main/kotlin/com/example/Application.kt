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
import java.time.Duration

fun main() {
    val port = 8080//Integer.parseInt(System.getenv("PORT"))

    embeddedServer(Netty, port = port) {
install(ContentNegotiation) {
    json()
}
        configureRouting()
        configureSerialization()
        websockConfigure()
    }.start(wait = true)
}

fun Application.websockConfigure(){
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/echo") {
            send("Please enter your name")
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val receivedText = frame.readText()
                        if (receivedText.equals("bye", ignoreCase = true)) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        } else {
                            send(Frame.Text("Hi, $receivedText!"))
                        }
                    }
                }
            }
        }
    }
}
