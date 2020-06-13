package com.example.demo.controller

import com.example.demo.data.db.SqlRepository
import com.example.demo.data.model.*
import com.example.demo.utils.toTimestamp
import javafx.collections.ObservableList
import tornadofx.*
import java.time.LocalDate

class UserController : Controller() {

    private val userSqlRepository by lazy { SqlRepository(UsersTbl, UserTbl) }

    private var userModel = UserViewModel()

    private val listOfUsers: ObservableList<UserViewModel> = userSqlRepository.transactionSelectAll {
        it.map {
            userModel.apply { item = it.toUserEntry() }
        }.observable()
    }

    var items: ObservableList<UserViewModel> by singleAssign()

    init {
        items = listOfUsers
    }

    fun add(
            newName: String,
            newSurname: String,
            newAddress: String,
            newGender: Boolean,
            newDateOfBirth: LocalDate,
            newTelephone: String
    ): UserEntry? {
        val newEntry = userSqlRepository.transactionInsert {
            name = newName
            surname = newSurname
            address = newAddress
            gender = newGender
            dateOfBirth = newDateOfBirth.toTimestamp()
            telephone = newTelephone
        }
        return newEntry?.toUserEntry()
    }

    fun update(updatedItem: UserViewModel): Int? {
        return userSqlRepository.transactionSingleUpdate(updatedItem.id.value.toInt()) {
            it[Name] = updatedItem.name.value
            it[Surname] = updatedItem.surname.value
            it[Address] = updatedItem.address.value
            it[Gender] = updatedItem.gender.value
            it[DateOfBirth] = updatedItem.dateOfBirth.value.toTimestamp()
            it[Telephone] = updatedItem.telephone.value
        }
    }

    fun delete(userItem: UserEntry) = userSqlRepository.deleteById(userItem.id)
}