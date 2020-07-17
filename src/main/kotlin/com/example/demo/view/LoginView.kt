package com.example.demo.view

import com.example.demo.app.Styles
import com.example.demo.controller.AuthController
import com.example.demo.data.model.MedicalStaffModel
import com.example.demo.utils.defaultPadding
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.scene.control.Label
import tornadofx.*

class LoginView : View("Login") {

    private val medicalStaff = MedicalStaffModel()

    private val authController by inject<AuthController>()

    private val validationMessage = SimpleStringProperty("")
    private var validationLabel: Label by singleAssign()

    override val root = form {
        fieldset(title, labelPosition = Orientation.VERTICAL) {
            field("Username") {
                textfield(medicalStaff.username).required()
            }
            field("Password") {
                passwordfield(medicalStaff.password).required()
            }
            validationLabel = label(validationMessage) {
                addClass(Styles.error)
            }
            buttonbar {
                button("Log in") {
                    isDefaultButton = true
                    enableWhen(medicalStaff.valid)
                    action { authController.login(medicalStaff) }
                }
            }
        }

        paddingAll = defaultPadding

        authController.usernameError.onChange {
            if (it) validationMessage.set("The username was not found") else validationLabel.hide()
        }
        authController.passwordError.onChange {
            if (it) validationMessage.set("Invalid password") else validationLabel.hide()
        }
    }
}