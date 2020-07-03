@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object MedicationsTbl : IdTable<Int>() {
    override val id = reference("MedicationID", ActesTbl.id, onDelete = ReferenceOption.CASCADE)
    var CounterStock = long("CounterStock")
    var WarehouseStock = long("WarehouseStock")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class Medication(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Medication>(MedicationsTbl)

    var counterStock by MedicationsTbl.CounterStock
    var warehouseStock by MedicationsTbl.WarehouseStock
}

fun ResultRow.toMedicationEntry(includeActe: Boolean = true) = MedicationEntry(
        this[MedicationsTbl.id].value,
        this[MedicationsTbl.CounterStock],
        this[MedicationsTbl.WarehouseStock],
        if (includeActe) this.toActeEntry(includeActe) else null
)

fun MedicationModel.toRow(): MedicationsTbl.(UpdateBuilder<*>) -> Unit = {
    it[id] = EntityID(this@toRow.id.value.toInt(), MedicationsTbl)
    it[CounterStock] = this@toRow.counterStock.value.toLong()
    it[WarehouseStock] = this@toRow.warehouseStock.value.toLong()
}

class MedicationEntry(id: Int, counterStock: Long?, warehouseStock: Long?, acteEntry: ActeEntry?) {
    var idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val acte = ActeModel().apply { item = acteEntry }

    var counterStockProperty = SimpleLongProperty(counterStock ?: -1)
    val counterStock by counterStockProperty

    var warehouseStockProperty = SimpleLongProperty(warehouseStock ?: -1)
    val warehouseStock by warehouseStockProperty
}

class MedicationModel : ItemViewModel<MedicationEntry>() {
    val id = bind { item?.idProperty }
    val name = bind { item?.acte?.name }
    val appliedAmount = bind { item?.acte?.appliedAmount }
    val officialAmount = bind { item?.acte?.officialAmount }
    val counterStock = bind { item?.counterStockProperty }
    val warehouseStock = bind { item?.warehouseStockProperty }

    val synthesisSectionId = bind { item?.acte?.synthesisSectionId }
    val synthesisSectionName = bind { item?.acte?.synthesisSectionName }
}

fun MedicationModel.toActeModel(ignoreId: Boolean = false): ActeModel = ActeModel().apply {
    item = ActeEntry(
            if (ignoreId) 0 else this@toActeModel.id.value.toInt(),
            this@toActeModel.name.value,
            this@toActeModel.appliedAmount.value.toDouble(),
            this@toActeModel.officialAmount.value.toDouble(),
            SynthesisSectionEntry(
                    this@toActeModel.synthesisSectionId.value.toInt(),
                    this@toActeModel.synthesisSectionName.value
            ))
}