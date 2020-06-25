package com.example.demo.data.repository

import com.example.demo.data.model.*
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
            .join(MedicationsTbl, JoinType.LEFT, ActesTbl.id, MedicationsTbl.id)
            .join(SynthesisSectionsTbl, JoinType.LEFT, ActesTbl.SynthesisSection, SynthesisSectionsTbl.id)
            .select {
                notExists(MedicationsTbl.select {
                    MedicationsTbl.id eq ActesTbl.id
                })
            }.map { ActeModel().apply { item = it.toActeEntry() } }

    override fun deleteAll() = ActesTbl.deleteAll()
}