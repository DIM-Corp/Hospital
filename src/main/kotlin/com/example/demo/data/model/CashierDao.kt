@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object Cashiers : IntIdTable() {
    override val id = MedicalStaffs.integer("CashierID").entityId() references MedicalStaffs.id
    val Type = integer("Type")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class Cashier(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Cashier>(Cashiers)

    var type by Cashiers.Type

    var orders by Order via CashierOrders
}