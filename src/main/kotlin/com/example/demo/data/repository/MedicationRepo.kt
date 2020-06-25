package com.example.demo.data.repository

import com.example.demo.data.model.*
import org.jetbrains.exposed.sql.*

class MedicationRepo : CrudRepository<MedicationModel, Int> {

    override fun create(entry: MedicationModel): MedicationModel {
        val result = MedicationsTbl.insert(entry.toRow()).resultedValues?.first()?.toMedicationEntry(false)
        return entry.apply { id.value = result?.id }
    }

    override fun update(vararg entries: MedicationModel): Iterable<MedicationModel> {
        for (entry in entries) {
            if (MedicationsTbl.select { MedicationsTbl.id eq entry.id.value.toInt() }.count() == 0L) continue
            MedicationsTbl.update({ MedicationsTbl.id eq entry.id.value.toInt() }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: MedicationModel) = MedicationsTbl
            .deleteWhere { MedicationsTbl.id eq entry.id.value.toInt() }

    override fun find(id: Int) = listOf(MedicationModel().apply {
        item = MedicationsTbl
                .join(ActesTbl, JoinType.LEFT, ActesTbl.id, MedicationsTbl.id)
                .join(SynthesisSectionsTbl, JoinType.LEFT, ActesTbl.SynthesisSection, SynthesisSectionsTbl.id)
                .select { MedicationsTbl.id eq id }.firstOrNull()?.toMedicationEntry()
    })

    override fun findAll() = MedicationsTbl
            .join(ActesTbl, JoinType.LEFT, ActesTbl.id, MedicationsTbl.id)
            .join(SynthesisSectionsTbl, JoinType.LEFT, ActesTbl.SynthesisSection, SynthesisSectionsTbl.id)
            .selectAll()
            .map { MedicationModel().apply { item = it.toMedicationEntry() } }

    override fun deleteAll() = MedicationsTbl.deleteAll()
}