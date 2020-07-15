package com.example.demo.controller

import com.example.demo.data.model.MedicalStaffModel
import tornadofx.*

class MedicalStaffController : Controller() {

    private val stateController by inject<StateController>()

    fun login(user: MedicalStaffModel) {
        preferences {
            putBoolean("isLoggedIn", true)
            stateController.isLoggedIn.value = getBoolean("isLoggedIn", false)
        }
    }
}