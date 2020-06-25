package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.UserModel
import com.example.demo.data.repository.UserRepo
import javafx.collections.ObservableList
import tornadofx.*

class UserController : Controller() {

    private val userRepo: UserRepo by di()

    private val listOfUsers: ObservableList<UserModel> = execute { userRepo.findAll() }.observable()

    var items: ObservableList<UserModel> by singleAssign()

    init {
        items = listOfUsers
    }

    fun add(newItem: UserModel) {
        execute {
            listOfUsers.add(userRepo.create(newItem))
        }
    }

    fun update(updatedItem: UserModel) {
        execute {
            userRepo.update(updatedItem)
        }
    }

    fun delete(userItem: UserModel) = execute { userRepo.delete(userItem) }
}