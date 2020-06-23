package com.example.demo.app

import com.example.demo.controller.*
import com.example.demo.view.ActesView
import com.example.demo.view.CreateBillView
import com.example.demo.view.OrdersView
import javafx.scene.control.TabPane
import tornadofx.*

class HospitalWorkspace : Workspace("Efoulan", NavigationMode.Tabs) {

    init {
        // Controllers
        UserController()
        ActesController()
        OrderController()
        OrderItemsController()
        PatientController()

        tabContainer.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        header.hide()
    }

    override fun onBeforeShow() {
        super.onBeforeShow()
        dock<CreateBillView>()
        dock<ActesView>()
        dock<OrdersView>()
        tabContainer.selectionModel.select(0)
    }
}
