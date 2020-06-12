@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
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
object SynthesisSectionsTbl : IntIdTable() {
    override val id = integer("ActeId").entityId()
    val Name = varchar("Name", 64)
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class SynthesisSectionTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SynthesisSectionTbl>(SynthesisSectionsTbl)

    var name by SynthesisSectionsTbl.Name
}

class SynthesisSectionEntry(id: Int, name: String) {
    val idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val nameProperty = SimpleStringProperty(name)
    val name by idProperty
}

class SynthesisSectionViewModel : ItemViewModel<SynthesisSectionEntry>() {
    val id = bind { item?.idProperty }
    val name = bind { item?.nameProperty }
}