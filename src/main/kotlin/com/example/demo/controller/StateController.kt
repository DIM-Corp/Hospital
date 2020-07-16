package com.example.demo.controller

import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

class StateController : Controller() {

    var isLoggedIn = SimpleBooleanProperty(false)
}