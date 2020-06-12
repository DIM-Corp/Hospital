@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
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
object ActesTbl : IdTable<Int>() {
    override val id = integer("ActeId").autoIncrement().entityId()
    val Name = text("Name")
    val AppliedAmount = decimal("AppliedAmount", scale = 0, precision = 9)
    val OfficialAmount = decimal("OfficialAmount", scale = 0, precision = 9)
    val SynthesisSection = reference("SynthesisSectionId", SynthesisSectionsTbl, fkName = "FK_Acte_Belongs")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class ActeTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ActeTbl>(ActesTbl)

    var name by ActesTbl.Name
    var appliedAmount by ActesTbl.AppliedAmount
    var officialAmount by ActesTbl.OfficialAmount
    var synthesisSection by ActesTbl.SynthesisSection
}

fun ResultRow.toActeEntry() = ActeEntry(
        this[ActesTbl.id].value,
        this[ActesTbl.Name],
        this[ActesTbl.AppliedAmount].toDouble(),
        this[ActesTbl.OfficialAmount].toDouble(),
        SynthesisSectionTbl(this[ActesTbl.SynthesisSection]).readValues.toSynthesisSectionEntry()
)

class ActeEntry(
        id: Int,
        name: String,
        appliedAmount: Double,
        officialAmount: Double,
        synthesisSection: SynthesisSectionEntry
) {
    val idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val nameProperty = SimpleStringProperty(name)
    val name by nameProperty

    val appliedAmountProperty = SimpleDoubleProperty(appliedAmount)
    val appliedAmount by appliedAmountProperty

    val officialAmountProperty = SimpleDoubleProperty(officialAmount)
    val officialAmount by officialAmountProperty

    val synthesisSectionProperty = SimpleObjectProperty(synthesisSection)
    val synthesisSection by synthesisSectionProperty
}

class ActeViewModel : ItemViewModel<ActeEntry>() {
    val id = bind { item?.idProperty }
    val name = bind { item?.nameProperty }
    val appliedAmount = bind { item?.appliedAmountProperty }
    val officialAmount = bind { item?.officialAmountProperty }
    val synthesisSection = bind { item?.synthesisSectionProperty }
}