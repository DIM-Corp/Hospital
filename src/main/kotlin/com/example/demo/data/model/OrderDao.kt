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
import org.jetbrains.exposed.sql.statements.UpdateBuilder
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

class Order(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : EntityClass<UUID, Order>(OrdersTbl)

    var timeStamp by OrdersTbl.Timestamp
    var patient by OrdersTbl.Patient

    var orderItems by Acte via OrderItemsTbl
}

fun ResultRow.toOrderEntry(includePatient: Boolean = true) = OrderEntry(
        if (includePatient) this.toPatientEntry() else null,
        this[OrdersTbl.id].value.toString().toUpperCase(),
        this[OrdersTbl.Timestamp].toLocalDateTime()
)

fun OrderModel.toRow(): OrdersTbl.(UpdateBuilder<*>) -> Unit = {
    it[id] = EntityID(UUID.randomUUID(), OrdersTbl)
    it[Patient] = EntityID(this@toRow.patientId.value.toInt(), PatientsTbl)
    it[Timestamp] = LocalDateTime.now().toDateTime()
}

class OrderEntry(patient: PatientEntry?, id: String? = null, date: LocalDateTime = LocalDateTime.now()) {
    val idProperty = SimpleStringProperty(id)
    val id by idProperty

    val dateProperty = SimpleObjectProperty(date)
    val date by dateProperty

    val patient = PatientModel().apply { item = patient }
}

class OrderModel : ItemViewModel<OrderEntry>() {
    val id = bind { item?.idProperty }
    val date = bind { item?.dateProperty }
    val patientId = bind { item?.patient?.id }
    val patientName = bind { item?.patient?.name }
}

fun OrderModel.getPatientModel() = PatientModel().apply {
    id.value = this@getPatientModel.patientId.value
    name.value = this@getPatientModel.patientName.value
}