package com.example.demo.data.repository

import com.example.demo.data.model.ActeModel
import com.example.demo.data.model.ActesTbl
import com.example.demo.data.model.toActeEntry
import com.example.demo.data.model.toRow
import org.jetbrains.exposed.sql.*

class ActesRepo : CrudRepository<ActeModel, Int> {

    override fun create(entry: ActeModel): ActeModel {
        val result = ActesTbl.insert(entry.toRow()).resultedValues?.first()?.toActeEntry(false)
        return entry.apply { id.value = result?.id }
    }

    override fun update(vararg entries: ActeModel): Iterable<ActeModel> {
        for (entry in entries) {
            if (ActesTbl.select { ActesTbl.id eq entry.id.value.toInt() }.count() == 0L) continue
            ActesTbl.update({ ActesTbl.id eq entry.id.value.toInt() }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: ActeModel) = ActesTbl.deleteWhere { ActesTbl.id eq entry.id.value.toInt() }

    override fun find(id: Int) = listOf(ActeModel().apply {
        item = ActesTbl.select { ActesTbl.id eq id }.firstOrNull()?.toActeEntry()
    })

    override fun findAll() = ActesTbl
            .selectAll()
            .map { ActeModel().apply { item = it.toActeEntry() } }

    override fun deleteAll() = ActesTbl.deleteAll()
}