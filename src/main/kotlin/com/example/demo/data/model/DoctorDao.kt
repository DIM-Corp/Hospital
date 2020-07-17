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
object DoctorsTbl : IdTable<Int>() {
    override val id = reference("DoctorID", MedicalStaffsTbl)
    val Speciality = reference("SpecialityID", SpecialitiesTbl, fkName = "FK_Doctor_Speciality")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class Doctor(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Doctor>(DoctorsTbl)

    var orders by Order via DoctorOrdersTbl
}

fun ResultRow.toDoctorEntry(ignoreJoin: Boolean = false) = DoctorEntry(
        this[DoctorsTbl.id].value,
        if (ignoreJoin) null else this.toSpecialityEntry(),
        if (ignoreJoin) null else this.toUserEntry()
)

class DoctorEntry(id: Int, speciality: SpecialityEntry?, user: UserEntry?) {
    var idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val speciality = SpecialityModel().apply { item = speciality }
    val user = UserModel().apply { item = user }
}

class DoctorModel : ItemViewModel<DoctorEntry>() {
    val id = bind { item?.idProperty }

    // User Data
    val name = bind { item?.user?.name }
    val address = bind { item?.user?.address }
    val gender = bind { item?.user?.gender }
    val age = bind { item?.user?.age }
    val telephone = bind { item?.user?.telephone }

    // Speciality Data
    val specialityId = bind { item?.speciality?.id }
    val specialityLabel = bind { item?.speciality?.name }
}

fun DoctorModel.toRow(): DoctorsTbl.(UpdateBuilder<*>) -> Unit = {
    it[id] = EntityID(1, MedicalStaffsTbl)
    it[Speciality] = EntityID(this@toRow.specialityId.value.toInt(), MedicalStaffsTbl)
}