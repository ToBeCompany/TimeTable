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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration

fun main() {
    val port = 8008//Integer.parseInt(System.getenv("PORT"))
//    Database.connect(
//        "jdbc:postgresql://ec2-54-204-128-96.compute-1.amazonaws.com:5432/d43qcb6atqt206?sslmode=require",
//        driver = "org.postgresql.Driver",
//        user = "zpikpxknmoscgo",
//        password ="7d2459c6a2a75d029f3ecaf7b4564bbf0114f96083a4e504886771cb212c344f"
//    )

    Database.connect(
        "jdbc:postgresql://localhost:5432/timetablee",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "password"
    )

    embeddedServer(Netty, port = port) {
        install(ContentNegotiation) {
            json()
        }
        configureRouting()
        configureSerialization()
        configureSockets()
    }.start(wait = true)
}


