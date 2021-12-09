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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.LinkedHashSet

fun Application.configureSockets() {
    var idperson = 0
    var idpassenger = 0
    var idVodila = 0
    var coordinates = 0


//    var collection_Names = mutableMapOf(1 to "viktor",2 to "vo",3 to "serega")
    var collection_Names = mutableMapOf("vi" to 11233412,"se" to 23878123,"vova" to 43231432)
    var list_of_names = listOf<String>("vi","se")


    var listsOflists = mutableMapOf<Int,MutableList<Int>>(
        1 to mutableListOf(),
        2 to mutableListOf(),
        3 to mutableListOf()
    )


    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        webSocket("/passenger/{idpassenger}") {
            send("укажите маршрут")
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val receivedText = frame.readText()
                        idpassenger = receivedText.toInt()

                        listsOflists[receivedText.toInt()]?.add(receivedText.toInt())
                        println(listsOflists[receivedText.toInt()])
//                        for(i in collection_Names.keys) {
//                            if (i == receivedText){
//                                idperson = collection_Names[i]!!
//
//                                namePerson = receivedText
                        GlobalScope.launch {
                        while(true){
                            delay(3000)
                        send("coordinates ${coordinates}")}
                        }
//                                send(Frame.Text("Ваше имя есть в списке, $receivedText!"))
//                            }
//
//                        }


                    }
                }
            }


                    }




    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/voditel/{idVodila}") {
            println("Водитель в сети")

            val thisConnection = Connection(this)
            connections += thisConnection


//            for(i in list_of_names){
//                if(i == namePerson){
//                    valueOfId += collection_Names[i].toString()!!
//                    println(valueOfId)
//                }
//
//            }

            try {
                send("введите по какому маршруту в поедите")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    idVodila = receivedText.toInt()

                  listsOflists[idVodila]?.forEach {
                      GlobalScope.launch {
                          while(true){
                              delay(2000)
                            coordinates++
                          }
                      }
                  }


                }

            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection


                idperson = 0
            }
        }


        }



}

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)

    }

    val name = "user${lastId.getAndIncrement()}"
}