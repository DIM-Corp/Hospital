@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 05:34:28 AM
 */
object DoctorOrdersTbl : Table() {
    val Order = reference("OrderID", OrdersTbl, fkName = "FK_DocServe_Order")
    val Doctor = reference("DoctorID", DoctorsTbl, fkName = "FK_DocServe_Doctor")
}

fun ResultRow.toDoctorOrderEntry() = DoctorOrderEntry(
        Order(this[DoctorOrdersTbl.Order]).readValues.toOrderEntry(),
        Doctor(this[DoctorOrdersTbl.Doctor]).readValues.toDoctorEntry()
)

class DoctorOrderEntry(order: OrderEntry, doctor: DoctorEntry) {
    val order = OrderModel().apply { item = order }

    val doctor = DoctorModel().apply { item = doctor }
}

class DoctorOrderModel : ItemViewModel<DoctorOrderEntry>() {
    val order = bind { item?.order?.id }
    val doctor = bind { item?.doctor?.id }
}