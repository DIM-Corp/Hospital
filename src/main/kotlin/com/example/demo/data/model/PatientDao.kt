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
object Patients : IntIdTable() {
    override val id = integer("PatientID").entityId() references UsersTbl.id
    val Condition = integer("Condition")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class Patient(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Patient>(Patients)

    var condition by Patients.Condition
}
