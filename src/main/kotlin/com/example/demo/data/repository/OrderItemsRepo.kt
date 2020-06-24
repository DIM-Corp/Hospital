package com.example.demo.data.repository

import com.example.demo.data.model.OrderItemEntry
import com.example.demo.data.model.OrderItemsTbl
import com.example.demo.data.model.toOrderItemEntry
import com.example.demo.data.model.toRow
import org.jetbrains.exposed.sql.*
import java.util.*

class OrderItemsRepo : CrudRepository<OrderItemEntry, String> {

    override fun create(entry: OrderItemEntry): OrderItemEntry {
        OrderItemsTbl.insert(entry.toRow())
        return entry
    }

    override fun update(vararg entries: OrderItemEntry): Iterable<OrderItemEntry> {
        for (entry in entries) {
            if (OrderItemsTbl.select { OrderItemsTbl.Order eq UUID.fromString(entry.order.id.value) }.count() == 0L) continue
            OrderItemsTbl.update({ OrderItemsTbl.Order eq UUID.fromString(entry.order.id.value) }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: OrderItemEntry) = OrderItemsTbl.deleteWhere { OrderItemsTbl.Order eq UUID.fromString(entry.order.id.value) }

    override fun find(id: String) = OrderItemsTbl.select { OrderItemsTbl.Order eq UUID.fromString(id) }.map { it.toOrderItemEntry() }

    override fun findAll() = OrderItemsTbl.selectAll().map { it.toOrderItemEntry() }

    override fun deleteAll() = OrderItemsTbl.deleteAll()
}