@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import java.time.LocalDate

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object OrdersTbl : IntIdTable() {
    override val id = integer("ActeId").entityId()
    val Timestamp = long("Timestamp")
    val Patient = reference("PatientID", PatientsTbl, fkName = "FK_Order_Patient")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class OrderTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<OrderTbl>(OrdersTbl)

    var timeStamp by OrdersTbl.Timestamp
    var patient by OrdersTbl.Patient

    var orderItems by ActeTbl via OrderItemsTbl
}

class OrderEntry(id: Int, date: LocalDate, patient: PatientEntry) {
    val idProperty = SimpleIntegerProperty(id)
    val dateProperty = SimpleObjectProperty(date)
    val patientProperty = SimpleObjectProperty(patient)
}