@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import org.jetbrains.exposed.sql.Table
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 05:05:28 AM
 */
object OrderItemsTbl : Table() {
    val Acte = reference("ActeID", ActesTbl, fkName = "FK_OrderItems_Acte")
    val Order = reference("OrderID", OrdersTbl, fkName = "FK_OrderItems_Order")
    val Quantity = integer("Quantity")
}

class OrderItemEntry(acte: ActeEntry, order: OrderEntry, quantity: Int) {
    val acteProperty = SimpleObjectProperty(acte)
    val acte by acteProperty

    val orderProperty = SimpleObjectProperty(order)
    val order by orderProperty

    val quantityProperty = SimpleIntegerProperty(quantity)
    val quantity by quantityProperty
}

class OrderItem : ItemViewModel<OrderItemEntry>() {
    val acte = bind { item?.acteProperty }
    val order = bind { item?.orderProperty }
    val quantity = bind { item?.quantityProperty }
}