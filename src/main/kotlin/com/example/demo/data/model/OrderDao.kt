@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.jodatime.datetime
import org.joda.time.LocalDateTime
import tornadofx.*
import java.util.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object OrdersTbl : UUIDTable(columnName = "OrderId") {
    val Timestamp = datetime("Timestamp")
    val Patient = reference("PatientID", PatientsTbl, fkName = "FK_Order_Patient")
}

class OrderTbl(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : EntityClass<UUID, OrderTbl>(OrdersTbl)

    var timeStamp by OrdersTbl.Timestamp
    var patient by OrdersTbl.Patient

    var orderItems by ActeTbl via OrderItemsTbl
}

fun ResultRow.toOrderEntry() = OrderEntry(
        this[OrdersTbl.id].value.toString().toUpperCase(),
        this[OrdersTbl.Timestamp].toLocalDateTime(),
        this.toPatientEntry()
)

fun ResultRow.toOrderEntryNoPatient() = OrderEntry(
        this[OrdersTbl.id].value.toString(),
        this[OrdersTbl.Timestamp].toLocalDateTime(),
        null
)

class OrderEntry(id: String, date: LocalDateTime, patient: PatientEntry?) {
    val idProperty = SimpleStringProperty(id)
    val id by idProperty

    val dateProperty = SimpleObjectProperty(date)
    val date by dateProperty

    val patient = PatientEntryModel().apply { item = patient }
}

class OrderViewModel : ItemViewModel<OrderEntry>() {
    val id = bind { item?.idProperty }
    val date = bind { item?.dateProperty }
    val patientId = bind { item?.patient?.id }
    val patientName = bind { item?.patient?.name }
}