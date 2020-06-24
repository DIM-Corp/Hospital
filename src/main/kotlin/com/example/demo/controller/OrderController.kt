package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.*
import javafx.collections.ObservableList
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import tornadofx.*

class OrderController : Controller() {

    private val listOfOrders: ObservableList<OrderModel> = execute {
        OrdersTbl.join(PatientsTbl, JoinType.LEFT, OrdersTbl.Patient, PatientsTbl.id)
                .join(UsersTbl, JoinType.LEFT, PatientsTbl.id, UsersTbl.id)
                .selectAll()
                .orderBy(OrdersTbl.Timestamp, SortOrder.DESC).map {
                    OrderModel().apply { item = it.toOrderEntry() }
                }
    }.observable()

    var items: ObservableList<OrderModel> by singleAssign()

    init {
        items = listOfOrders
    }

    fun addOrder(order: OrderModel) {
        listOfOrders.add(order)
    }
}