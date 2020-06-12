@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleObjectProperty
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
        OrderTbl(this[DoctorOrdersTbl.Order]).readValues.toOrderEntry(),
        DoctorTbl(this[DoctorOrdersTbl.Doctor]).readValues.toDoctorEntry()
)

class DoctorOrderEntry(order: OrderEntry, doctor: DoctorEntry) {
    val orderProperty = SimpleObjectProperty(order)
    val order by orderProperty

    val doctorProperty = SimpleObjectProperty(doctor)
    val doctor by doctorProperty
}

class DoctorOrderViewModel : ItemViewModel<DoctorOrderEntry>() {
    val order = bind { item?.orderProperty }
    val doctor = bind { item?.doctorProperty }
}