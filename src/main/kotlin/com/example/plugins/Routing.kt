package com.example.plugins

import com.example.MarshMTime
import com.example.MarshrutM
import com.example.directoryObjects.*
import com.example.marshruts
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like

import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureRouting() {
    routing {


        get("/sign/{id_sign}") {
            val siginId = call.parameters["id_sign"]
            val a = transaction {
                val t = Users.select() {
                    Users.idnum eq siginId.toString()
                }.find { it[Users.idnum] == siginId.toString() }
                t?.get(Users.idnum)?.let { it2 -> UserM(it2, t[Users.name], t[Users.user_type]) }
            }
            call.respondText(Json.encodeToString<UserM?>(a), ContentType.Application.Json)
        }

        get("/namesMarsh") {
            val a = transaction {
                marshNames.selectAll().map {
                    (MarshNamesM(it[marshNames.id], it[marshNames.name]))
                }
            }
            call.respondText(Json.encodeToString<List<MarshNamesM>>(a), ContentType.Application.Json)
        }

        get("/OneMarsh/{id}") {
            val res = call.parameters["id"]?.toString() ?: ""
            val l = mutableListOf<busstposCorrestion>()
            val li = mutableListOf<List<String>>()
            val a = transaction {
                val marshname = marshNames.select(marshNames.id eq res).map { it[marshNames.name] }.first()
                val marshid = marshNames.select(marshNames.name eq marshname).map { it[marshNames.id] }.first()

                marshruts.select(marshruts.idm eq res)
                    .map {
                        l.add(
                            (busstposCorrestion(
                                it[marshruts.id],
                                listOf(it[marshruts.idost], it[marshruts.idostplus])
                            ))
                        )
                    }
                l.sortBy { it.id }
                l.forEach { li.add(it.first) }

                var list = li.flatten().distinct()
                var lis = mutableListOf<bustoptimewith>()
                var listGeoPosFalse = mutableListOf<List<geopos>>()

                for (i in list) {
                    var secBuffer = marshruts.select(marshruts.idm.eq(res) and marshruts.idost.eq(i))
                        .map { MarshMTime(clock = it[marshruts.clock]) }.first().clock
                    lis.add(busStop.select(busStop.id eq i)
                        .map {
                            bustoptimewith(
                                BusStopM(
                                    it[busStop.id],
                                    it[busStop.name],
                                    geopos(
                                        it[busStop.lat],
                                        it[busStop.lng]
                                    )
                                ),
                                secBuffer
                            )
                        }
                        .first())
                }

                listGeoPosFalse.add(
                    traictori.select(traictori.idm eq res)
                        .map { geopos(it[traictori.lat], it[traictori.long]) })


                var listGeoPosTrue = listGeoPosFalse.flatten()

                marshruts.select(marshruts.idm eq res)
                    .map { (MarshrutM(marshid, marshname, lis, listGeoPosTrue)) }
                    .first()
            }
            call.respondText(Json.encodeToString<MarshrutM>(a), ContentType.Application.Json)
        }


        get("/marsh") {
            val result = listsOfChannels.keys.toList()
            call.respondText(Json.encodeToString<List<Int>>(result), ContentType.Application.Json)
        }

        delete("/asd") {
            call.respondText("asd")
        }

        delete("/deleteUser/{id}") {
            var param = call.parameters["id"]?.toInt()

            transaction {
                usern.deleteWhere{ usern.id eq param}
            }
            call.respondText("Deleted ---- $param")

        }

    }
}