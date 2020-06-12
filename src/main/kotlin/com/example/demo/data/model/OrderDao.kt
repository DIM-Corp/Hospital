@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import tornadofx.*
import java.time.LocalDate

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object OrdersTbl : IdTable<Int>() {
    override val id = integer("OrderId").autoIncrement().entityId()
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

fun ResultRow.toOrderEntry() = OrderEntry(
        this[OrdersTbl.id].value,
        this[OrdersTbl.Timestamp],
        PatientTbl(this[OrdersTbl.Patient]).readValues.toPatientEntry()
)

class OrderEntry(id: Int, date: LocalDate, patient: PatientEntry) {
    val idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val dateProperty = SimpleObjectProperty(date)
    val date by dateProperty

    val patientProperty = SimpleObjectProperty(patient)
    val patient by patientProperty
}

class OrderViewModel : ItemViewModel<OrderEntry>() {
    val id = bind { item?.idProperty }
    val date = bind { item?.dateProperty }
    val patient = bind { item?.patientProperty }
}