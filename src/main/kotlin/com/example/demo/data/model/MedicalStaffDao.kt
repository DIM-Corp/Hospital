@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object MedicalStaffsTbl : IntIdTable() {
    override val id = integer("Matriculation").entityId() references UsersTbl.id
    var Username = varchar("Username", 32)
    var Password = varchar("Password", 32)
    var Service = reference("ServiceID", Services, fkName = "FK_MedicalStaff_WorksIn")
    var Role = integer("Role")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class MedicalStaffTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicalStaffTbl>(MedicalStaffsTbl)

    var username by MedicalStaffsTbl.Username
    var password by MedicalStaffsTbl.Password
    var service by MedicalStaffsTbl.Service
    var role by MedicalStaffsTbl.Role
}

class MedicalStaffEntry(id: Int, username: String, password: String, role: Int, service: ServiceEntry) {
    var id = SimpleIntegerProperty(id)
    var username = SimpleStringProperty(username)
    var password = SimpleStringProperty(password)
    var role = SimpleIntegerProperty(role)
    var service = SimpleObjectProperty(service)
}