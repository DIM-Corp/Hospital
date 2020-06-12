@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object MedicationsTbl : IntIdTable() {
    override val id = integer("MedicationID").entityId() references ActesTbl.id
    var CounterStock = long("CounterStock")
    var WarehouseStock = long("WarehouseStock")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class MedicationTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicationTbl>(MedicationsTbl)

    var counterStock by MedicationsTbl.CounterStock
    var warehouseStock by MedicationsTbl.WarehouseStock
}

class MedicationEntry(id: Int, counterStock: Long, warehouseStock: Long) {
    var idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    var counterStockProperty = SimpleLongProperty(counterStock)
    val counterStock by counterStockProperty

    var warehouseStockProperty = SimpleLongProperty(warehouseStock)
    val warehouseStock by warehouseStockProperty
}