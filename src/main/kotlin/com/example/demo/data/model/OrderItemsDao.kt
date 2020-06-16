@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import org.jetbrains.exposed.sql.ResultRow
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

fun ResultRow.toOrderItemEntry() = OrderItemEntry(
        this.toActeEntry(),
        this.toOrderEntry(),
        this[OrderItemsTbl.Quantity]
)

class OrderItemEntry(acte: ActeEntry, order: OrderEntry, quantity: Int) {
    val acte = ActeViewModel().apply { item = acte }
    val order = OrderViewModel().apply { item = order }

    val quantityProperty = SimpleIntegerProperty(quantity)
    val quantity by quantityProperty
}

class OrderItemModel : ItemViewModel<OrderItemEntry>() {
    val label = bind { item?.acte?.name }
    val price = bind { item?.acte?.appliedAmount }
    val quantity = bind { item?.quantityProperty }
    var timeStamp = bind { item?.order?.date }

    val acteId = bind { item?.acte?.id }
    val orderId = bind { item?.order?.id }

    val qtyTemp = bind { SimpleIntegerProperty(1) }
    val amtCalc = bind { SimpleDoubleProperty(0.0) }

    init {
        qtyTemp.addListener { _, _, new ->
            val qty: Int = new.toInt()
            val p: Double = price.value.toDouble()
            amtCalc.value = qty * p
        }
    }
}