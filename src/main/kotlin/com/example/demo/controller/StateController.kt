package com.example.demo.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.stage.Window
import tornadofx.*

class StateController : Controller() {

    var isLoggedIn = SimpleBooleanProperty(false)

    var currentWindow: Window? = null
}