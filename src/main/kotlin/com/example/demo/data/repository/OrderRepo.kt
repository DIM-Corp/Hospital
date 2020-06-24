package com.example.demo.data.repository

import com.example.demo.data.model.*
import org.jetbrains.exposed.sql.*
import java.util.*

class OrderRepo : CrudRepository<OrderEntry, String> {

    override fun create(entry: OrderEntry): OrderEntry {
        OrdersTbl.insert(entry.toRow())
        return entry
    }

    override fun update(vararg entries: OrderEntry): Iterable<OrderEntry> {
        for (entry in entries) {
            if (OrdersTbl.select { OrdersTbl.id eq UUID.fromString(entry.id) }.count() == 0L) continue
            OrdersTbl.update({ OrdersTbl.id eq UUID.fromString(entry.id) }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: OrderEntry) = OrdersTbl.deleteWhere { OrdersTbl.id eq UUID.fromString(entry.id) }

    override fun find(id: String) = listOf(OrdersTbl.select { OrdersTbl.id eq UUID.fromString(id) }.firstOrNull()?.toOrderEntry())

    override fun findAll() = OrdersTbl
            .join(PatientsTbl, JoinType.LEFT, OrdersTbl.Patient, PatientsTbl.id)
            .join(UsersTbl, JoinType.LEFT, PatientsTbl.id, UsersTbl.id)
            .selectAll().map { it.toOrderEntry() }

    override fun deleteAll() = OrdersTbl.deleteAll()
}
