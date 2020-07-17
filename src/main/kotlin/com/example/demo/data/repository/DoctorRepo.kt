package com.example.demo.data.repository

import com.example.demo.data.model.*
import org.jetbrains.exposed.sql.*

class DoctorRepo : CrudRepository<DoctorModel, Int> {

    override fun create(entry: DoctorModel): DoctorModel {
        val result = DoctorsTbl.insert(entry.toRow()).resultedValues?.first()?.toDoctorEntry(true)
        return entry.apply { id.value = result?.id }
    }

    override fun update(vararg entries: DoctorModel): Iterable<DoctorModel> {
        for (entry in entries) {
            if (DoctorsTbl.select { DoctorsTbl.id eq entry.id.value.toInt() }.count() == 0L) continue
            DoctorsTbl.update({ DoctorsTbl.id eq entry.id.value.toInt() }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: DoctorModel) = DoctorsTbl.deleteWhere { DoctorsTbl.id eq entry.id.value.toInt() }

    override fun find(id: Int) = listOf(DoctorModel().apply {
        item = DoctorsTbl
                .join(UsersTbl, JoinType.LEFT, PatientsTbl.id, UsersTbl.id)
                .join(SpecialitiesTbl, JoinType.LEFT, DoctorsTbl.id, SpecialitiesTbl.id)
                .select { DoctorsTbl.id eq id }
                .firstOrNull()?.toDoctorEntry()
    })

    override fun findAll() = DoctorsTbl
            .join(UsersTbl, JoinType.LEFT, PatientsTbl.id, UsersTbl.id)
            .join(SpecialitiesTbl, JoinType.LEFT, DoctorsTbl.id, SpecialitiesTbl.id)
            .selectAll()
            .map { DoctorModel().apply { item = it.toDoctorEntry() } }

    override fun deleteAll() = DoctorsTbl.deleteAll()
}