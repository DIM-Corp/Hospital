@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object CashiersTbl : IntIdTable() {
    override val id = integer("CashierID").entityId() references MedicalStaffsTbl.id
    val Type = integer("Type")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class CashierTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CashierTbl>(CashiersTbl)

    var type by CashiersTbl.Type

    var orders by Order via CashierOrders
}

class CashierEntry(id: Int, type: Int) {
    val idProperty = SimpleIntegerProperty(id)
    val typeProperty = SimpleIntegerProperty(type)
}