package com.example.demo.data.repository

import com.example.demo.data.model.UserModel
import com.example.demo.data.model.UsersTbl
import com.example.demo.data.model.toRow
import com.example.demo.data.model.toUserEntry
import org.jetbrains.exposed.sql.*

class UserRepo : CrudRepository<UserModel, Int> {

    override fun create(entry: UserModel): UserModel {
        val result = UsersTbl.insert(entry.toRow()).resultedValues?.first()?.toUserEntry()
        return entry.apply { id.value = result?.id }
    }

    override fun update(vararg entries: UserModel): Iterable<UserModel> {
        for (entry in entries) {
            if (UsersTbl.select { UsersTbl.id eq entry.id.value.toInt() }.count() == 0L) continue
            UsersTbl.update({ UsersTbl.id eq entry.id.value.toInt() }, body = entry.toRow())
        }
        return entries.asIterable()
    }

    override fun delete(entry: UserModel) = UsersTbl.deleteWhere { UsersTbl.id eq entry.id.value.toInt() }

    override fun find(id: Int) = listOf(UserModel().apply {
        item = UsersTbl.select { UsersTbl.id eq id }.firstOrNull()?.toUserEntry()
    })

    override fun findAll() = UsersTbl
            .selectAll()
            .map { UserModel().apply { item = it.toUserEntry() } }

    override fun deleteAll() = UsersTbl.deleteAll()
}