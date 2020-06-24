package com.example.demo.data.repository

import com.example.demo.data.model.ActeEntry
import com.example.demo.data.model.ActesTbl
import com.example.demo.data.model.toActeEntry
import com.example.demo.data.model.toRow
import org.jetbrains.exposed.sql.*

class ActesRepo : CrudRepository<ActeEntry, Int> {

    override fun create(entry: ActeEntry): ActeEntry {
        ActesTbl.insert(entry.toRow())
        return entry
    }

    override fun update(vararg entries: ActeEntry): Iterable<ActeEntry> {
        for (entry in entries) {
            if (ActesTbl.select { ActesTbl.id eq entry.id }.count() == 0L) continue
            ActesTbl.update({ ActesTbl.id eq entry.id }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: ActeEntry) = ActesTbl.deleteWhere { ActesTbl.id eq entry.id }

    override fun find(id: Int) = listOf(ActesTbl.select { ActesTbl.id eq id }.firstOrNull()?.toActeEntry())

    override fun findAll() = ActesTbl.selectAll().map { it.toActeEntry() }

    override fun deleteAll() = ActesTbl.deleteAll()
}