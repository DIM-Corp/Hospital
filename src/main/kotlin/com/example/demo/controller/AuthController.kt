package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.MedicalStaffModel
import com.example.demo.data.repository.MedicalStaffRepo
import com.example.demo.utils.HashingUtils
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class AuthController : Controller() {

    private val hashingUtils: HashingUtils by di()

    private val stateController by inject<StateController>()

    private val medicalStaffRepo: MedicalStaffRepo by di()

    val usernameError = SimpleBooleanProperty(false)
    val passwordError = SimpleBooleanProperty(false)

    fun login(user: MedicalStaffModel) = execute {

        val autUser = medicalStaffRepo.findByUsername(user)

        if (autUser == null) usernameError.set(true)
        else if (!hashingUtils.verify(user.password.value, autUser.password)) passwordError.set(true)
        else {
            usernameError.set(false)
            passwordError.set(false)
            stateController.isLoggedIn.value = !stateController.isLoggedIn.value
        }
    }

}