@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object CashiersTbl : IdTable<Int>() {
    override val id = integer("CashierID").entityId() references MedicalStaffsTbl.id
    val Type = integer("Type")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class Cashier(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Cashier>(CashiersTbl)

    var type by CashiersTbl.Type

    var orders by Order via CashierOrdersTbl
}

fun ResultRow.toCashierEntry(ignoreJoin: Boolean = false) = CashierEntry(
        this[CashiersTbl.id].value,
        this[CashiersTbl.Type],
        if (ignoreJoin) null else this.toUserEntry()
)

class CashierEntry(id: Int, type: Int, user: UserEntry?) {
    val idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val typeProperty = SimpleIntegerProperty(type)
    val type by typeProperty

    val user = UserModel().apply { item = user }
}

class CashierModel : ItemViewModel<CashierEntry>() {
    val id = bind { item?.idProperty }
    val type = bind { item?.typeProperty }

    // User Data
    val name = bind { item?.user?.name }
    val address = bind { item?.user?.address }
    val gender = bind { item?.user?.gender }
    val age = bind { item?.user?.age }
    val telephone = bind { item?.user?.telephone }
}

fun CashierModel.toRow(): CashiersTbl.(UpdateBuilder<*>) -> Unit = {
    it[id] = EntityID(1, CashiersTbl)
    it[Type] = this@toRow.type.value.toInt()
}