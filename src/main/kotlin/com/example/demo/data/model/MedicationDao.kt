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
object Medications : IntIdTable() {
    override val id = integer("MedicationID").entityId() references ActesTbl.id
    var CounterStock = long("CounterStock")
    var WarehouseStock = long("WarehouseStock")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class Medication(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Medication>(Medications)

    var counterStock by Medications.CounterStock
    var warehouseStock by Medications.WarehouseStock
}