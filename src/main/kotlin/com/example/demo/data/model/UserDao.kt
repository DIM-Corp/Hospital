@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import tornadofx.*
import java.time.LocalDate

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object UsersTbl : IntIdTable() {
    override val id = integer("UserID").entityId()
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

class UserEntry(id: Int, name: String, surname: String, address: String, gender: Boolean, dateOfBirth: LocalDate, telephone: String) {
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

    val dateOfBirthProperty = SimpleObjectProperty(dateOfBirth)
    val dateOfBirth by dateOfBirthProperty

    val telephoneProperty = SimpleStringProperty(telephone)
    val telephone by telephoneProperty
}
