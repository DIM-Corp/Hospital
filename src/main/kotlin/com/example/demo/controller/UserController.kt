package com.example.demo.controller

import com.example.demo.data.db.SqlRepository
import com.example.demo.data.model.*
import com.example.demo.utils.toMillis
import javafx.collections.ObservableList
import tornadofx.*

class UserController : Controller() {

    private val userSqlRepository by lazy { SqlRepository(UsersTbl, UserTbl) }

    private val listOfUsers: ObservableList<UserViewModel> = userSqlRepository.transactionSelectAll {
        it.map {
            UserViewModel().apply { item = it.toUserEntry() }
        }.observable()
    }

    var items: ObservableList<UserViewModel> by singleAssign()

    init {
        items = listOfUsers
    }

    fun add(
            newName: String,
            newAddress: String?,
            newGender: Boolean,
            newAge: Int,
            newTelephone: String
    ): UserEntry? {
        val newEntry = userSqlRepository.transactionNew {
            name = newName
            address = newAddress
            gender = newGender
            dateOfBirth = newAge.toMillis()
            telephone = newTelephone
        }
        return newEntry?.toUserEntry().also {
            listOfUsers.add(UserViewModel().apply { item = it })
        }
    }

    fun update(updatedItem: UserViewModel): Int? {
        return userSqlRepository.transactionSingleUpdate(updatedItem.id.value.toInt()) {
            it[Name] = updatedItem.name.value
            it[Address] = updatedItem.address.value
            it[Gender] = updatedItem.gender.value
            it[DateOfBirth] = updatedItem.age.value.toMillis()
            it[Telephone] = updatedItem.telephone.value
        }
    }

    fun delete(userItem: UserEntry) = userSqlRepository.deleteById(userItem.id)
}