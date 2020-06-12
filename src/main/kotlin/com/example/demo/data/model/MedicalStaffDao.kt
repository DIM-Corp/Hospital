@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object MedicalStaffs : IntIdTable() {
    override val id = integer("Matriculation").entityId() references Users.id
    var Username = varchar("Username", 32)
    var Password = varchar("Password", 32)
    var Service = reference("ServiceID", Services, fkName = "FK_MedicalStaff_WorksIn")
    var Role = integer("Role")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class MedicalStaff(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicalStaff>(MedicalStaffs)

    var username by MedicalStaffs.Username
    var password by MedicalStaffs.Password
    var service by MedicalStaffs.Service
    var role by MedicalStaffs.Role
}