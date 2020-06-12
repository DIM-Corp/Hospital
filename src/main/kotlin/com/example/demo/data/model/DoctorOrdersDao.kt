@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleObjectProperty
import org.jetbrains.exposed.sql.Table

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 05:34:28 AM
 */
object DoctorOrdersTbl : Table() {
    val Order = reference("OrderID", OrdersTbl, fkName = "FK_DocServe_Order")
    val Doctor = reference("DoctorID", DoctorsTbl, fkName = "FK_DocServe_Doctor")
}

class DoctorOrderEntry(order: OrderEntry, doctor: DoctorEntry) {
    val orderProperty = SimpleObjectProperty(order)
    val doctorProperty = SimpleObjectProperty(doctor)
}