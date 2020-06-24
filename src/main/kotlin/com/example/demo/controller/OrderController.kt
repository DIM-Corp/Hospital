package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.OrderModel
import com.example.demo.data.repository.OrderRepo
import javafx.collections.ObservableList
import tornadofx.*

class OrderController : Controller() {

    private val orderRepo: OrderRepo by di()

    private val listOfOrders: ObservableList<OrderModel> = execute {
        orderRepo.findAll().map {
            OrderModel().apply { item = it }
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