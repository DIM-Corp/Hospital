@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object PatientsTbl : IdTable<Int>() {
    override val id = integer("PatientID").entityId() references UsersTbl.id
    val Condition = integer("Condition")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class PatientTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PatientTbl>(PatientsTbl)

    var condition by PatientsTbl.Condition
}

class PatientEntry(id: Int, condition: Int) {
    val idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val conditionProperty = SimpleIntegerProperty(condition)
    val condition by conditionProperty
}

class PatientViewModel : ItemViewModel<PatientEntry>() {
    val id = bind { item?.idProperty }
    val condition = bind { item?.conditionProperty }
}