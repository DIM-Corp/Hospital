@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import org.jetbrains.exposed.sql.Table

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 05:34:28 AM
 */
object CashierOrders : Table() {
    val Order = reference("OrderID", Orders, fkName = "FK_CreateOrder_Order")
    val Cashier = reference("CashierID", Cashiers, fkName = "FK_CreateOrder_Cashier")
}