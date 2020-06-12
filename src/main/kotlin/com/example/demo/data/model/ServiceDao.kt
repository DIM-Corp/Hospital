@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object ServicesTbl : IdTable<Int>() {
    override val id = integer("ServiceID").autoIncrement().entityId()
    val Name = varchar("Name", 64)
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class ServiceTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ServiceTbl>(ServicesTbl)

    var name by ServicesTbl.Name
}

fun ResultRow.toServiceEntry() = ServiceEntry(
        this[ServicesTbl.id].value,
        this[ServicesTbl.Name]
)

class ServiceEntry(id: Int, name: String) {
    val idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val nameProperty = SimpleStringProperty(name)
    val name by nameProperty
}

class ServiceViewModel : ItemViewModel<ServiceEntry>() {
    val id = bind { item?.idProperty }
    val name = bind { item?.nameProperty }
}