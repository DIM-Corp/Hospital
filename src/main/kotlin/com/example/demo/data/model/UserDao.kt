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
object Users : IntIdTable() {
    override val id = integer("UserID").entityId()
    val Name = varchar("Name", 32)
    val Surname = varchar("Surname", 32)
    val Address = text("Address")
    val Gender = bool("Gender")
    val DateOfBirth = long("DateOfBirth")
    val Telephone = varchar("Telephone", 9)
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

open class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.Name
    var surname by Users.Surname
    var address by Users.Address
    var gender by Users.Gender
    var dateOfBirth by Users.DateOfBirth
    var telephone by Users.Telephone
}
