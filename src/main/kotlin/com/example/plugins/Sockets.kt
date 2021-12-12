package com.example.plugins

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.*


fun Application.configureSockets() {


    var listsOfChannels = Collections.synchronizedMap<Int, BroadcastChannel<Frame>>(mutableMapOf())
    val stateflow: MutableStateFlow<String> = MutableStateFlow("")

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {

        webSocket("/passenger/{idpassenger}") {
            val idpassenger = call.parameters["idpassenger"]?.toInt() ?: 0

            launch {
                listsOfChannels[idpassenger]?.let {
                    it.consumeEach { frame->
                        send(frame)
                    }
                }

            }.join()
        }
        webSocket("/driver/{idDriver}") {
            val idDriver = call.parameters["idDriver"]?.toInt() ?: 0
            val broadcastChannel = Channel<Frame>()
            listsOfChannels.put(idDriver, broadcastChannel.broadcast())
            for (frame in incoming){
                launch {
                    broadcastChannel.send(frame.copy())
                    println(listsOfChannels[idDriver])
                }
            }

        }
    }
}

