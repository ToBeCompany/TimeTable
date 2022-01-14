package com.example.directoryObjects

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object marshNames : Table("table_name"){
    val id = varchar("id",100)
    val name = varchar("name",100)
}
@Serializable
data class  MarshNamesM(val id: String = "",val name:String = "")

