package com.example.plugins

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table("directory") {
    val id = integer("id") // Column<String>
    val name = varchar("name", 20) // Column<String>
}
@Serializable
data class UserM(val id : Int, val name : String)
fun Application.configureRouting() {

    routing {
        get("/{id}") {
            var ids = call.parameters["id"]?.toInt() ?: 0

            val a = transaction {
                Users.selectAll().map {
                    (UserM(it[Users.id], it[Users.name]))
                }
            }
            call.respondText(Json.encodeToString<UserM>(a[ids]))
        }

        }

}
