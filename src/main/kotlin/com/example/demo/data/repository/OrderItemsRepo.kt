package com.example.demo.data.repository

import com.example.demo.data.model.*
import org.jetbrains.exposed.sql.*
import java.util.*

class OrderItemsRepo : CrudRepository<OrderItemModel, String> {

    override fun create(entry: OrderItemModel): OrderItemModel {
        OrderItemsTbl.insert(entry.toRow())
        return entry
    }

    override fun update(vararg entries: OrderItemModel): Iterable<OrderItemModel> {
        for (entry in entries) {
            if (OrderItemsTbl.select { OrderItemsTbl.Order eq UUID.fromString(entry.orderId.value) }.count() == 0L) continue
            OrderItemsTbl.update({ OrderItemsTbl.Order eq UUID.fromString(entry.orderId.value) }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: OrderItemModel) = OrderItemsTbl.deleteWhere { OrderItemsTbl.Order eq UUID.fromString(entry.orderId.value) }

    override fun find(id: String) = OrderItemsTbl
            .innerJoin(ActesTbl)
            .innerJoin(OrdersTbl)
            .select { OrderItemsTbl.Order eq UUID.fromString(id) }
            .map { OrderItemModel().apply { item = it.toOrderItemEntry() } }

    override fun findAll() = OrderItemsTbl
            .selectAll()
            .map { OrderItemModel().apply { item = it.toOrderItemEntry() } }

    override fun deleteAll() = OrderItemsTbl.deleteAll()
}