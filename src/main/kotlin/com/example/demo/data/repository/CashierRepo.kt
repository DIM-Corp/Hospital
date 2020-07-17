package com.example.demo.data.repository

import com.example.demo.data.model.*
import org.jetbrains.exposed.sql.*

class CashierRepo : CrudRepository<CashierModel, Int> {

    override fun create(entry: CashierModel): CashierModel {
        val result = CashiersTbl.insert(entry.toRow()).resultedValues?.first()?.toCashierEntry(true)
        return entry.apply { id.value = result?.id }
    }

    override fun update(vararg entries: CashierModel): Iterable<CashierModel> {
        for (entry in entries) {
            if (CashiersTbl.select { CashiersTbl.id eq entry.id.value.toInt() }.count() == 0L) continue
            CashiersTbl.update({ CashiersTbl.id eq entry.id.value.toInt() }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: CashierModel) = CashiersTbl.deleteWhere { CashiersTbl.id eq entry.id.value.toInt() }

    override fun find(id: Int) = listOf(CashierModel().apply {
        item = CashiersTbl
                .join(UsersTbl, JoinType.LEFT, CashiersTbl.id, UsersTbl.id)
                .select { CashiersTbl.id eq id }
                .firstOrNull()?.toCashierEntry()
    })

    override fun findAll() = CashiersTbl
            .join(UsersTbl, JoinType.LEFT, CashiersTbl.id, UsersTbl.id)
            .selectAll()
            .map { CashierModel().apply { item = it.toCashierEntry() } }

    override fun deleteAll() = CashiersTbl.deleteAll()
}