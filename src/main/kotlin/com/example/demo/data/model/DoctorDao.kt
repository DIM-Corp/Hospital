@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object DoctorsTbl : IdTable<Int>() {
    override val id = MedicalStaffsTbl.integer("DoctorID").entityId() references MedicalStaffsTbl.id
    val Speciality = reference("SpecialityID", SpecialitiesTbl, fkName = "FK_Doctor_Speciality")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class DoctorTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<DoctorTbl>(DoctorsTbl)

    var orders by OrderTbl via DoctorOrdersTbl
}

fun ResultRow.toDoctorEntry() = DoctorEntry(
        this[DoctorsTbl.id].value,
        this.toSpecialityEntry()
)

class DoctorEntry(id: Int, speciality: SpecialityEntry) {
    var idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    var specialityProperty = SimpleObjectProperty(speciality)
    val speciality by specialityProperty
}

class DoctorViewModel : ItemViewModel<DoctorEntry>() {
    val id = bind { item?.idProperty }
    val speciality = bind { item?.specialityProperty }
}