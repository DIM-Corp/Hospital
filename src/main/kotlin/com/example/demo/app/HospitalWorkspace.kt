package com.example.demo.app

import com.example.demo.controller.ActesController
import com.example.demo.controller.OrderController
import com.example.demo.controller.UserController
import com.example.demo.view.ActesView
import com.example.demo.view.CreateBillView
import javafx.scene.control.TabPane
import tornadofx.*

class HospitalWorkspace : Workspace("Efoulan", NavigationMode.Tabs) {

    init {
        // Controllers
        UserController()
        ActesController()
        OrderController()
        // Tabs
        dock<CreateBillView>()
        dock<ActesView>()
        tabContainer.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        header.hide()
    }

}
