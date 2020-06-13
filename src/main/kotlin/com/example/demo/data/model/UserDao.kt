@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import com.example.demo.utils.toAge
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
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
object UsersTbl : IdTable<Int>() {
    override val id = integer("UserID").autoIncrement().entityId()
    val Name = varchar("Name", 32)
    val Surname = varchar("Surname", 32)
    val Address = text("Address")
    val Gender = bool("Gender")
    val DateOfBirth = long("DateOfBirth")
    val Telephone = varchar("Telephone", 9)
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

open class UserTbl(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserTbl>(UsersTbl)

    var name by UsersTbl.Name
    var surname by UsersTbl.Surname
    var address by UsersTbl.Address
    var gender by UsersTbl.Gender
    var dateOfBirth by UsersTbl.DateOfBirth
    var telephone by UsersTbl.Telephone
}

fun ResultRow.toUserEntry() = UserEntry(
        this[UsersTbl.id].value,
        this[UsersTbl.Name],
        this[UsersTbl.Surname],
        this[UsersTbl.Address],
        this[UsersTbl.Gender],
        this[UsersTbl.DateOfBirth].toAge(),
        this[UsersTbl.Telephone]
)

class UserEntry(id: Int, name: String, surname: String, address: String, gender: Boolean, age: Int, telephone: String) {
    val idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    val nameProperty = SimpleStringProperty(name)
    val name by nameProperty

    val surnameProperty = SimpleStringProperty(surname)
    val surname by surnameProperty

    val addressProperty = SimpleStringProperty(address)
    val address by addressProperty

    val genderProperty = SimpleBooleanProperty(gender)
    val gender by genderProperty

    val ageProperty = SimpleObjectProperty(age)
    val age by ageProperty

    val telephoneProperty = SimpleStringProperty(telephone)
    val telephone by telephoneProperty
}

class UserViewModel : ItemViewModel<UserEntry>() {
    val id = bind { item?.idProperty }
    val name = bind { item?.nameProperty }
    val surname = bind { item?.surnameProperty }
    val address = bind { item?.addressProperty }
    val gender = bind { item?.genderProperty }
    val age = bind { item?.ageProperty }
    val telephone = bind { item?.telephoneProperty }
}