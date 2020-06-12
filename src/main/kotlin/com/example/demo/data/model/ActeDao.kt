@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object ActesTbl : IntIdTable() {
    override val id = integer("ActeId").entityId()
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

class ActeEntry(
        id: Int,
        name: String,
        appliedAmount: Double,
        officialAmount: Double,
        synthesisSection: SynthesisSectionEntry
) {
    val idProperty = SimpleIntegerProperty(id)
    val nameProperty = SimpleStringProperty(name)
    val appliedAmountProperty = SimpleDoubleProperty(appliedAmount)
    val officialAmountProperty = SimpleDoubleProperty(officialAmount)
    val synthesisSectionProperty = SimpleObjectProperty(synthesisSection)
}