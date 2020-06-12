@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import org.jetbrains.exposed.sql.Table

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 05:34:28 AM
 */
object DoctorOrders : Table() {
    val Order = reference("OrderID", OrdersTbl, fkName = "FK_DocServe_Order")
    val Doctor = reference("DoctorID", Doctors, fkName = "FK_DocServe_Doctor")
}