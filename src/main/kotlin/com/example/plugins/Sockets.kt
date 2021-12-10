package com.example.plugins

import com.example.voditel
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import java.time.*
import io.ktor.application.*
import io.ktor.network.sockets.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.sessions.*

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import java.nio.channels.Channels
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

fun Application.configureSockets() {

    //var listsOflists = mutableMapOf<Int, MutableList<DefaultWebSocketSession>>()
    var listsOfChannels = mutableMapOf<Int, ReceiveChannel<Frame>>()


    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {

        webSocket("/passenger/{idpassenger}") {
            val idpassenger = call.parameters["idpassenger"]?.toInt() ?: 0
//            listsOflists[idpassenger]?.add(this)
         var t =
            launch {
                 listsOfChannels[idpassenger]?.let {
                     for (i in it){

                         send(i)
                     }
                 }
            }.join()
            println("listssss ::;$listsOfChannels")
            println("final")
        }

        webSocket("/driver/{idDriver}") {
            val idDriver= call.parameters["idDriver"]?.toInt() ?: 0
            listsOfChannels.put(idDriver, incoming)
        println("incomig::$incoming")
            for (frame in incoming) {
                println("frame::$frame")
                println("incoming22::$incoming")

            }
            println("listchannel::$listsOfChannels")
           // println("listoflist::$listsOflists")
        }
    }
}
