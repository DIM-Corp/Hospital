@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleIntegerProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object PatientsTbl : IdTable<Int>() {
    override val id = reference("PatientID", UsersTbl, onDelete = ReferenceOption.CASCADE)
    val Condition = integer("Condition")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class Patient(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Patient>(PatientsTbl)

    var condition by PatientsTbl.Condition
}

fun ResultRow.toPatientEntry(includeUser: Boolean = true) = PatientEntry(
        this[PatientsTbl.id].value,
        this[PatientsTbl.Condition],
        if (includeUser) this.toUserEntry() else null
)

class PatientEntry(id: Int, condition: Int, user: UserEntry?) {
    val idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val conditionProperty = SimpleIntegerProperty(condition)
    val condition by conditionProperty

    val user = UserModel().apply { item = user }
}

class PatientModel : ItemViewModel<PatientEntry>() {
    val id = bind { item?.idProperty }
    val name = bind { item?.user?.name }
    val address = bind { item?.user?.address }
    val gender = bind { item?.user?.gender }
    val age = bind { item?.user?.age }
    val telephone = bind { item?.user?.telephone }
    val condition = bind { item?.conditionProperty }
}

fun PatientModel.toRow(): PatientsTbl.(UpdateBuilder<*>) -> Unit = {
    it[id] = EntityID(this@toRow.id.value.toInt(), UsersTbl)
    it[Condition] = this@toRow.condition.value.toInt()
}

fun PatientModel.toUserModel(): UserModel = UserModel().apply {
    item = UserEntry(
            this@toUserModel.id.value.toInt(),
            this@toUserModel.name.value,
            this@toUserModel.address.value,
            this@toUserModel.gender.value,
            this@toUserModel.age.value.toInt(),
            this@toUserModel.telephone.value
    )
}