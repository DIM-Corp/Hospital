@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object ActesTbl : IntIdTable(columnName = "ActeId") {
    val Name = text("Name")
    val AppliedAmount = decimal("AppliedAmount", scale = 0, precision = 9)
    val OfficialAmount = decimal("OfficialAmount", scale = 0, precision = 9)
    val SynthesisSection = reference("SynthesisSectionId", SynthesisSectionsTbl, fkName = "FK_Acte_Belongs")
}

class Acte(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Acte>(ActesTbl)

    var name by ActesTbl.Name
    var appliedAmount by ActesTbl.AppliedAmount
    var officialAmount by ActesTbl.OfficialAmount
    var synthesisSection by ActesTbl.SynthesisSection
}

fun ResultRow.toActeEntry(includeSection: Boolean = true) = ActeEntry(
        this[ActesTbl.id].value,
        this[ActesTbl.Name],
        this[ActesTbl.AppliedAmount].toDouble(),
        this[ActesTbl.OfficialAmount].toDouble(),
        if (includeSection) this.toSynthesisSectionEntry() else null
)

class ActeEntry(
        id: Int?,
        name: String,
        appliedAmount: Double,
        officialAmount: Double,
        synthesisSection: SynthesisSectionEntry?
) {
    val idProperty = SimpleIntegerProperty(id ?: 0)
    val id by idProperty

    val nameProperty = SimpleStringProperty(name)
    val name by nameProperty

    val appliedAmountProperty = SimpleDoubleProperty(appliedAmount)
    val appliedAmount by appliedAmountProperty

    val officialAmountProperty = SimpleDoubleProperty(officialAmount)
    val officialAmount by officialAmountProperty

    val synthesisSection = SynthesisSectionModel().apply { item = synthesisSection }
}

fun ActeModel.toRow(): ActesTbl.(UpdateBuilder<*>) -> Unit = {
    it[Name] = this@toRow.name.value
    it[AppliedAmount] = this@toRow.appliedAmount.value.toDouble().toBigDecimal()
    it[OfficialAmount] = this@toRow.officialAmount.value.toDouble().toBigDecimal()
    it[SynthesisSection] = EntityID(this@toRow.synthesisSectionId.value.toInt(), SynthesisSectionsTbl)
}

class ActeModel : ItemViewModel<ActeEntry>() {
    val id = bind { item?.idProperty }
    val name = bind { item?.nameProperty }
    val appliedAmount = bind { item?.appliedAmountProperty }
    val officialAmount = bind { item?.officialAmountProperty }
    val synthesisSectionId = bind { item?.synthesisSection?.id }
    val synthesisSectionName = bind { item?.synthesisSection?.name }
}

fun ActeModel.toMedicationModel() = MedicationModel().apply {
    item = MedicationEntry(
            this@toMedicationModel.id.value.toInt(),
            -1,
            -1,
            ActeEntry(
                    this@toMedicationModel.id.value.toInt(),
                    this@toMedicationModel.name.value,
                    this@toMedicationModel.appliedAmount.value.toDouble(),
                    this@toMedicationModel.officialAmount.value.toDouble(),
                    SynthesisSectionEntry(
                            this@toMedicationModel.synthesisSectionId.value.toInt(),
                            this@toMedicationModel.synthesisSectionName.value
                    )
            )
    )
}