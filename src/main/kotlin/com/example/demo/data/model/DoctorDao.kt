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
object Doctors : IntIdTable() {
    override val id = MedicalStaffsTbl.integer("DoctorID").entityId() references MedicalStaffsTbl.id
    val Speciality = reference("SpecialityID", Specialities, fkName = "FK_Doctor_Speciality")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class Doctor(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Doctor>(Doctors)

    var orders by OrderTbl via DoctorOrders
}