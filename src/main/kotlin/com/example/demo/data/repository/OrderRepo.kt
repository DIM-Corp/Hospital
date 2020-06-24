package com.example.demo.data.repository

import com.example.demo.data.model.*
import org.jetbrains.exposed.sql.*
import java.util.*

class OrderRepo : CrudRepository<OrderModel, String> {

    override fun create(entry: OrderModel): OrderModel {
        val result = OrdersTbl.insert(entry.toRow()).resultedValues?.first()?.toOrderEntry(false)
        return entry.apply { id.value = result?.id; date.value = result?.date }
    }

    override fun update(vararg entries: OrderModel): Iterable<OrderModel> {
        for (entry in entries) {
            if (OrdersTbl.select { OrdersTbl.id eq UUID.fromString(entry.id.value) }.count() == 0L) continue
            OrdersTbl.update({ OrdersTbl.id eq UUID.fromString(entry.id.value) }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: OrderModel) = OrdersTbl.deleteWhere { OrdersTbl.id eq UUID.fromString(entry.id.value) }

    override fun find(id: String) = listOf(OrderModel().apply {
        item = OrdersTbl.select { OrdersTbl.id eq UUID.fromString(id) }.firstOrNull()?.toOrderEntry()
    })

    override fun findAll() = OrdersTbl
            .join(PatientsTbl, JoinType.LEFT, OrdersTbl.Patient, PatientsTbl.id)
            .join(UsersTbl, JoinType.LEFT, PatientsTbl.id, UsersTbl.id)
            .selectAll().map { OrderModel().apply { item = it.toOrderEntry() } }

    override fun deleteAll() = OrdersTbl.deleteAll()
}
