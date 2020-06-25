package com.example.demo.data.repository

import com.example.demo.data.model.*
import org.jetbrains.exposed.sql.*

class PatientRepo : CrudRepository<PatientModel, Int> {

    override fun create(entry: PatientModel): PatientModel {
        val result = PatientsTbl.insert(entry.toRow()).resultedValues?.first()?.toPatientEntry(false)
        return entry.apply { id.value = result?.id }
    }

    override fun update(vararg entries: PatientModel): Iterable<PatientModel> {
        for (entry in entries) {
            if (PatientsTbl.select { PatientsTbl.id eq entry.id.value.toInt() }.count() == 0L) continue
            PatientsTbl.update({ PatientsTbl.id eq entry.id.value.toInt() }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: PatientModel) = PatientsTbl
            .deleteWhere { PatientsTbl.id eq entry.id.value.toInt() }

    override fun find(id: Int) = listOf(PatientModel().apply {
        item = PatientsTbl
                .join(UsersTbl, JoinType.LEFT, PatientsTbl.id, UsersTbl.id)
                .select { PatientsTbl.id eq id }.firstOrNull()?.toPatientEntry()
    })

    override fun findAll() = PatientsTbl
            .join(UsersTbl, JoinType.LEFT, PatientsTbl.id, UsersTbl.id)
            .selectAll()
            .map { PatientModel().apply { item = it.toPatientEntry() } }

    override fun deleteAll() = PatientsTbl.deleteAll()
}