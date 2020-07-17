@file:Suppress("MemberVisibilityCanBePrivate", "unused", "HasPlatformType")

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
object CashierOrdersTbl : Table() {
    val Order = reference("OrderID", OrdersTbl, fkName = "FK_CreateOrder_Order")
    val Cashier = reference("CashierID", CashiersTbl, fkName = "FK_CreateOrder_Cashier")
}

fun ResultRow.toCashierOrderEntry() = CashierOrderEntry(
        this.toOrderEntry(),
        this.toCashierEntry()
)

class CashierOrderEntry(order: OrderEntry, cashier: CashierEntry) {
    val orderProperty = SimpleObjectProperty(order)
    val order by orderProperty

    val cashierProperty = SimpleObjectProperty(cashier)
    val cashier by cashierProperty
}

class CashierOrderModel : ItemViewModel<CashierOrderEntry>() {
    val order = bind { item?.orderProperty }
    val cashier = bind { item?.cashierProperty }
}

// TODO: Create Repo for CashierOrders