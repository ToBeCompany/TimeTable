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
import org.jetbrains.exposed.sql.transactions.transaction

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
data class  MarshNamesM(val id: String = "",val name:String = "")
@Serializable
data class UserM(val id : String = "", val name : String = "", val User_type:String = "")
@Serializable
data class MarshrutM(val id : String, val idm_Foreign:String,val idOst:String,val idOstPlus:String)
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
        get ("/OneMarsh/{id}"){
            val res = call.parameters["id"]?.toInt() ?: 0
            val a = transaction {
            val t = marshNames.select(){
                marshNames.id eq res.toString()
            }.find { it[marshNames.id] == res.toString()}

            t?.get(marshNames.id)?.let{it2 -> MarshNamesM(it2,t[marshNames.name])}
            }
            call.respondText(Json.encodeToString<MarshNamesM?>(a), ContentType.Application.Json)

        }





        get("/marsh") {
            val result = listsOfChannels.keys.toList()


                call.respondText(Json.encodeToString<List<Int>>(result), ContentType.Application.Json)


        }


    }


}


