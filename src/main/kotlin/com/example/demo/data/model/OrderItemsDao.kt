@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import tornadofx.*
import java.util.*

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
        this.toActeEntry(false),
        this.toOrderEntry(false),
        this[OrderItemsTbl.Quantity]
)

fun OrderItemModel.toRow(): OrderItemsTbl.(UpdateBuilder<*>) -> Unit = {
    it[Acte] = EntityID(this@toRow.acteId.value.toInt(), ActesTbl)
    it[Order] = EntityID(UUID.fromString(this@toRow.orderId.value), OrdersTbl)
    it[Quantity] = this@toRow.qtyTemp.value.toInt()
}

class OrderItemEntry(acte: ActeEntry, order: OrderEntry, quantity: Int) {
    val acte = ActeModel().apply { item = acte }
    val order = OrderModel().apply { item = order }

    val quantityProperty = SimpleIntegerProperty(quantity)
    val quantity by quantityProperty

    val amtProperty = SimpleDoubleProperty(quantity * acte.appliedAmount)
    val amt by amtProperty

    var totalAmount = Bindings.add(acte.appliedAmountProperty, 0)
}

class OrderItemModel : ItemViewModel<OrderItemEntry>() {
    val label = bind { item?.acte?.name }
    val price = bind { item?.acte?.appliedAmount }
    val quantity = bind { item?.quantityProperty }
    var timeStamp = bind { item?.order?.date }
    val amount = bind { item?.amtProperty }

    val acteId = bind { item?.acte?.id }
    val orderId = bind { item?.order?.id }

    val qtyTemp = SimpleIntegerProperty(1)

    var totalAmount = itemProperty.select(OrderItemEntry::totalAmount)

    init {
        qtyTemp.addListener { _, _, new ->
            val qty: Int = new.toInt()
            val p: Double = price.value.toDouble()
            amount.value = (qty * p)
        }
    }
}