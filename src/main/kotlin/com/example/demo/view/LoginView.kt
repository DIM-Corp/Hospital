package com.example.demo.view

import com.example.demo.controller.AuthController
import com.example.demo.data.model.MedicalStaffModel
import javafx.geometry.Orientation
import tornadofx.*

class LoginView : View("Login") {

    private val medicalStaff = MedicalStaffModel()

    private val authController by inject<AuthController>()

    override val root = form {
        fieldset(title, labelPosition = Orientation.VERTICAL) {
            field("Username") {
                textfield(medicalStaff.username).required()
            }
            field("Password") {
                passwordfield(medicalStaff.password).required()
            }
            buttonbar {
                button("Log in") {
                    isDefaultButton = true
                    enableWhen(medicalStaff.valid)
                    action { authController.login(medicalStaff) }
                }
            }
        }
    }
}