package com.example.demo.view

import javafx.geometry.Orientation
import tornadofx.*

class MainView : View("Login") {
    override val root = form {
        fieldset(title, labelPosition = Orientation.VERTICAL) {
            field("Username") {
                textfield()
            }
            field("Password") {
                passwordfield()
            }
            buttonbar {
                button("Log in") {
                    isDefaultButton = true
                }
            }
        }
    }
}