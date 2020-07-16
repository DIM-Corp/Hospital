package com.example.demo.controller

import com.example.demo.data.model.MedicalStaffModel
import tornadofx.*

class AuthController : Controller() {

    private val stateController by inject<StateController>()

    fun login(user: MedicalStaffModel) {
        stateController.isLoggedIn.value = !stateController.isLoggedIn.value
    }

}