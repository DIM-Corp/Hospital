@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import com.example.demo.utils.HashingUtils
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
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
object MedicalStaffsTbl : IdTable<Int>() {
    override val id = integer("Matriculation").entityId() references UsersTbl.id
    var Username = varchar("Username", 32)
    var Password = varchar("Password", 32)
    var Service = reference("ServiceID", ServicesTbl, fkName = "FK_MedicalStaff_WorksIn")
    var Role = integer("Role")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class MedicalStaff(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicalStaff>(MedicalStaffsTbl)

    var username by MedicalStaffsTbl.Username
    var password by MedicalStaffsTbl.Password
    var service by MedicalStaffsTbl.Service
    var role by MedicalStaffsTbl.Role
}

fun ResultRow.toMedicalStaffEntry() = MedicalStaffEntry(
        this[MedicalStaffsTbl.id].value,
        this[MedicalStaffsTbl.Username],
        this[MedicalStaffsTbl.Password],
        this[MedicalStaffsTbl.Role],
        this.toServiceEntry()
)

class MedicalStaffEntry(id: Int, username: String, password: String, role: Int, service: ServiceEntry) {
    var idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    var usernameProperty = SimpleStringProperty(username)
    val username by usernameProperty

    var passwordProperty = SimpleStringProperty(password)
    val password by passwordProperty

    var roleProperty = SimpleIntegerProperty(role)
    val role by roleProperty

    var serviceProperty = SimpleObjectProperty(service)
    val service by serviceProperty
}

class MedicalStaffModel : ItemViewModel<MedicalStaffEntry>() {
    val id = bind { item?.idProperty }
    val username = bind { item?.usernameProperty }
    val password = bind { item?.passwordProperty }
    val role = bind { item?.roleProperty }
    val service = bind { item?.roleProperty }
}

fun MedicalStaffModel.toRow(hashingUtils: HashingUtils): MedicalStaffsTbl.(UpdateBuilder<*>) -> Unit {
    return {
        it[Password] = hashingUtils.hash(this@toRow.password.value)
        it[Service] = EntityID(this@toRow.service.value.toInt(), MedicalStaffsTbl)
        it[Role] = this@toRow.role.value.toInt()
    }
}