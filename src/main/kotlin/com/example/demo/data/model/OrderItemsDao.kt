@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import org.jetbrains.exposed.sql.Table

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 05:05:28 AM
 */
object OrderItems : Table() {
    val Acte = reference("ActeID", ActesTbl, fkName = "FK_OrderItems_Acte")
    val Order = reference("OrderID", Orders, fkName = "FK_OrderItems_Order")
    val Quantity = integer("Quantity")
}