@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object DoctorsTbl : IntIdTable() {
    override val id = MedicalStaffsTbl.integer("DoctorID").entityId() references MedicalStaffsTbl.id
    val Speciality = reference("SpecialityID", SpecialitiesTbl, fkName = "FK_Doctor_Speciality")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class DoctorTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DoctorTbl>(DoctorsTbl)

    var orders by OrderTbl via DoctorOrdersTbl
}

class DoctorEntry(id: Int, speciality: SpecialityEntry) {
    var idProperty = SimpleIntegerProperty(id)
    var specialityProperty = SimpleObjectProperty(speciality)
}