package com.example.plugins

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.utils.io.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

import java.sql.Time
object  busStop :Table("busstop"){
    val id = varchar("id",20)
    val name = varchar("name",30)
    val lat = float("lat")
    val lng = float("long")
}

object marshruts :Table("marsruts"){
    val id = varchar("id",20)
    val idm = varchar("idm",20)
    val idost = varchar("idost",20)
    val idostplus = varchar("idostplus",20)

}
object marshNames :Table("marsrutnames"){
    val id = varchar("id",20)
    val name = varchar("namemarsrut",30)
}
object Users : Table("directory") {

    val idnum = varchar("idNum",20) // Column<String>
    val name = varchar("name", 20) // Column<String>
    val user_type = varchar("user_Type",6)
}
@Serializable
data class geopos(val lat: Float,val long: Float)
@Serializable
data class busstposCorrestion(val first:List<String>,)
@Serializable
data class  BusStopM(val id: String = "",val name:String = "",val geopos :geopos)


@Serializable
data class  MarshNamesM(val id: String = "",val name:String = "")


@Serializable
data class UserM(val id : String = "", val name : String = "", val User_type:String = "")


@Serializable
data class MarshrutM(val id: String = "", val idm_Foreign: String = "", val idOst:List<BusStopM> ,val lineMarshtriectori :List<geopos> )


fun Application.configureRouting() {

    routing {
        get("/{id}") {
            var ids = call.parameters["id"]?.toInt() ?: 0

            val a = transaction {
                Users.selectAll().map {
                    (UserM(it[Users.idnum], it[Users.name],it[Users.user_type]))

                }
            }
            call.respondText(Json.encodeToString<UserM>(a[ids]), ContentType.Application.Json)
        }

        get("/sign/{id_sign}") {
            val sigin_id = call.parameters["id_sign"]?.toInt() ?: 0

            val a = transaction {


                val t = Users.select(){
                    Users.idnum eq sigin_id.toString()
                }.find { it[Users.idnum] == sigin_id.toString()}

                t?.get(Users.idnum)?.let{it2 -> UserM(it2,t[Users.name],t[Users.user_type])}
            }


            call.respondText(Json.encodeToString<UserM?>(a), ContentType.Application.Json)
        }

        get ("/namesMarsh"){
            val a = transaction {
                marshNames.selectAll().map{
                    (MarshNamesM(it[marshNames.id],it[marshNames.name]))

                }

            }
            call.respondText(Json.encodeToString<List<MarshNamesM>>(a), ContentType.Application.Json)
        }
        get("/OneMarshName/{id}"){
            val res = call.parameters["id"]?.toInt() ?: 0
            val a = transaction {
                val t = marshNames.select(){
                    marshNames.id eq res.toString()
                }.find{it[marshNames.id] == res.toString()}
            }

        }











        get ("/OneMarsh/{id}") {
            val res = call.parameters["id"]?.toString() ?: ""
            val l = mutableListOf<busstposCorrestion>()
            val li = mutableListOf<List<String>>()
            val a = transaction {
                val marshname = marshNames.select(marshNames.id eq res).map{it[marshNames.name]}.first()
                //   val busGetFirst = marshruts.select(marshruts.idm eq res).map{(MarshrutM(it[marshruts.idost]))}.first()
//                busStop.select(busStop.id eq busGetFirst.toString()).map { list.add(busGetFirst) }
//            t?.get(Users.idnum)?.let{it2 -> UserM(it2,t[Users.name],t[Users.user_type])}
                marshruts.select(marshruts.idm eq res).map{ l.add((busstposCorrestion(listOf(it[marshruts.idost],it[marshruts.idostplus]))))}
                l.forEach { li.add((it.first))  }

                var list = li.flatten().distinct()
                var lis = mutableListOf<List<BusStopM>>()
                var listGeoPosFalse = mutableListOf<List<geopos>>()
                val busstops = busStop.selectAll()
                for(i in list){
                    lis.add(busStop.select(busStop.id eq i).map { BusStopM(it[busStop.id], it[busStop.name],geopos(it[busStop.lat],it[busStop.lng])) })

                }
                for(i in list){
                    listGeoPosFalse.add(busStop.select(busStop.id eq i).map { geopos(it[busStop.lat],it[busStop.lng]) })

                }
                var listGeoPosTrue = listGeoPosFalse.flatten()
                var listof = lis.flatten()









//(BusStopM(it[busStop.name]))
                marshruts.select(marshruts.idm eq res).map{ (MarshrutM(it[marshruts.id],marshname,listof,listGeoPosTrue)) }.first()


            }



//                t?.get(marshruts.idm)?.let{it2 -> MarshrutM(it2,t[marshruts.idm],t[marshruts.idost],t[marshruts.idostplus])}
// (MarshrutM(it[marshruts.id],it[marshruts.idm],it[marshruts.idost],it[marshruts.idostplus]))

            call.respondText(Json.encodeToString<MarshrutM>(a), ContentType.Application.Json)

        }













        get("/marsh") {
            val result = listsOfChannels.keys.toList()


            call.respondText(Json.encodeToString<List<Int>>(result), ContentType.Application.Json)


        }


    }


}