package com.example.demo.data.repository

import com.example.demo.data.model.ServiceModel
import com.example.demo.data.model.ServicesTbl
import com.example.demo.data.model.toRow
import com.example.demo.data.model.toServiceEntry
import org.jetbrains.exposed.sql.*

class ServicesRepo : CrudRepository<ServiceModel, Int> {

    override fun create(entry: ServiceModel): ServiceModel {
        val result = ServicesTbl.insert(entry.toRow()).resultedValues?.first()?.toServiceEntry()
        return entry.apply { id.value = result?.id }
    }

    override fun update(vararg entries: ServiceModel): Iterable<ServiceModel> {
        for (entry in entries) {
            if (ServicesTbl.select { ServicesTbl.id eq entry.id.value.toInt() }.count() == 0L) continue
            ServicesTbl.update({ ServicesTbl.id eq entry.id.value.toInt() }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: ServiceModel) = ServicesTbl.deleteWhere { ServicesTbl.id eq entry.id.value.toInt() }

    override fun find(id: Int) = listOf(ServiceModel().apply {
        item = ServicesTbl.select { ServicesTbl.id eq id }.firstOrNull()?.toServiceEntry()
    })

    override fun findAll() = ServicesTbl
            .selectAll().map { ServiceModel().apply { item = it.toServiceEntry() } }

    override fun deleteAll() = ServicesTbl.deleteAll()
}
