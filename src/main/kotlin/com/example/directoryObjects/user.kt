package com.example.directoryObjects

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

object usern : IntIdTable(){

    val name = varchar("name",100)

}
@Serializable
data class UserDeletion(val id: Int = 0, val name: String = "" )
