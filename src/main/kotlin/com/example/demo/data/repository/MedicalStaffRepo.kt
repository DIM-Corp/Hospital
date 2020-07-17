package com.example.demo.data.repository

import com.example.demo.data.model.*
import com.example.demo.utils.HashingUtils
import org.jetbrains.exposed.sql.*
import tornadofx.*

class MedicalStaffRepo : CrudRepository<MedicalStaffModel, Int>, Component() {

    private val hashingUtils: HashingUtils by di()

    override fun create(entry: MedicalStaffModel): MedicalStaffModel {
        val result = MedicalStaffsTbl.insert(entry.toRow(hashingUtils)).resultedValues?.first()?.toMedicalStaffEntry(true)
        return entry.apply { id.value = result?.id }
    }

    override fun update(vararg entries: MedicalStaffModel): Iterable<MedicalStaffModel> {
        for (entry in entries) {
            if (MedicalStaffsTbl.select { MedicalStaffsTbl.id eq entry.id.value.toInt() }.count() == 0L) continue
            MedicalStaffsTbl.update({ MedicalStaffsTbl.id eq entry.id.value.toInt() }, body = entry.toRow(hashingUtils))
        }
        return entries.asIterable()
    }

    override fun delete(entry: MedicalStaffModel) = MedicalStaffsTbl.deleteWhere { MedicalStaffsTbl.id eq entry.id.value.toInt() }

    override fun find(id: Int) = listOf(MedicalStaffModel().apply {
        item = MedicalStaffsTbl
                .join(ServicesTbl, JoinType.LEFT, MedicalStaffsTbl.Service, ServicesTbl.id)
                .select { MedicalStaffsTbl.id eq id }
                .firstOrNull()?.toMedicalStaffEntry()
    })

    override fun findAll() = MedicalStaffsTbl
            .selectAll()
            .map { MedicalStaffModel().apply { item = it.toMedicalStaffEntry(true) } }

    override fun deleteAll() = MedicalStaffsTbl.deleteAll()

    fun findByUsername(entry: MedicalStaffModel) = MedicalStaffsTbl
            .select { MedicalStaffsTbl.Username eq entry.username.value }
            .firstOrNull()?.toMedicalStaffEntry(true)
}