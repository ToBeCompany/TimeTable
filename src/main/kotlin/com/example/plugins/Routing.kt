package com.example.plugins

import io.ktor.application.*
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
    val id = integer("id")
    val idm = integer("idm")
    val idost = integer("idost")
    val idostplus = integer("idostplus")
}

object Users : Table("directory") {

    val idnum = integer("idNum") // Column<String>
    val name = varchar("name", 20) // Column<String>
    val user_type = varchar("user_Type",6)
}
@Serializable
data class UserM(val id : Int = 0, val name : String = "", val User_type:String = "")
@Serializable
data class MarshrutM(val id : Int, val idm_Foreign:Int,val idOst:Int,val idOstPlus:Int)
fun Application.configureRouting() {

    routing {
        get("/{id}") {
            var ids = call.parameters["id"]?.toInt() ?: 0

            val a = transaction {
                Users.selectAll().map {
                    (UserM(it[Users.idnum], it[Users.name],it[Users.user_type]))

                }
            }
            call.respondText(Json.encodeToString<UserM>(a[ids]))
        }

        get("/sign/{id_sign}") {
            val sigin_id = call.parameters["id_sign"]?.toInt() ?: 0

            val a = transaction {

               val m =  Users.selectAll().find{
                    it[Users.idnum] == sigin_id}

                m?.get(Users.idnum)?.let { it1 -> UserM(it1,m[Users.name],m[Users.user_type]) }
            }

            call.respondText(Json.encodeToString<UserM?>(a))
                }
//                val answer = Users.select(Op.build {
//                    Users.idnum eq sigin_id
//                }).execute(this)
//                answer
//                (UserM(it[, it[Users.name],it[Users.user_type])








        get("/marsh") {
            val result = listsOfChannels.keys.toList()
            val a = transaction {
                marshruts.selectAll().map {
                    (MarshrutM(it[marshruts.id], it[marshruts.idm], it[marshruts.idost], it[marshruts.idostplus]))

                }
            }
            for (i in result) {

                call.respondText(Json.encodeToString<List<MarshrutM>>(listOf(a[i])))
            }

        }


    }


}


