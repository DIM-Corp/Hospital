package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.*
import com.example.demo.data.repository.OrderItemsRepo
import com.example.demo.data.repository.OrderRepo
import com.example.demo.utils.Item
import com.example.demo.utils.PrinterService
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.ObservableList
import org.joda.time.DateTime
import tornadofx.*

class OrderItemsController : Controller() {

    private val orderItemsRepo: OrderItemsRepo by di()
    private val orderRepo: OrderRepo by di()

    var selectedItems: ObservableList<MedicationModel> by singleAssign()
    var orderItems: ObservableList<OrderItemModel> by singleAssign()
    private var printerService: PrinterService by singleAssign()

    var orderItemsTotalProperty = SimpleDoubleProperty(0.0)

    private val orderController: OrderController by inject()

    private lateinit var selectedOrder: OrderEntry

    fun updateOrderTotal() {
        var total = 0.0
        try {
            orderItems.forEach { total += it.amount.value.toDouble() }
            orderItemsTotalProperty.set(total)
        } catch (e: Exception) {
            orderItemsTotalProperty.set(0.0)
        }
    }

    fun loadOrderItemsForOrder(uuid: String) = execute {
        orderItems.clear()
        val items = orderItemsRepo.find(uuid)
        selectedOrder = OrderEntry(null, items.first().orderId.value.toString(), items.first().timeStamp.value)
        orderItems.addAll(items)
        updateOrderTotal()
    }

    private fun createOrder(patientModel: PatientModel): Pair<String, DateTime> = execute {
        val o = orderRepo.create(OrderModel().apply { patientId.value = patientModel.id.value; patientName.value = patientModel.name.value })

        orderItems.forEach {
            it.orderId.value = o.id.value
            orderItemsRepo.create(it)
        }
        // Update orders view
        orderController.addOrder(o)
        // Return a pair (UUID, timestamp)
        Pair(o.id.value, o.date.value.toDateTime())
    }

    fun printOrder(patientModel: PatientModel, isNew: Boolean = true) {
        val pair = if (isNew) createOrder(patientModel)
        else Pair(selectedOrder.id, selectedOrder.date.toDateTime())

        printerService.printReceipt(
                orderItems.map { Item(it.label.value, it.qtyTemp.value, it.price.value.toDouble()) },
                patientModel.name.value,
                pair.first,
                pair.second
        )
        selectedItems.clear()
    }

    init {
        printerService = PrinterService()

        orderItems = mutableListOf<OrderItemModel>().observable()
        selectedItems = mutableListOf<MedicationModel>().observable()
        selectedItems.onChange { items ->
            while (items.next()) {
                if (items.wasAdded()) {
                    orderItems.addAll(items.addedSubList.map {
                        OrderItemModel().apply {
                            label.value = it?.name?.value
                            price.value = it?.appliedAmount?.value
                            quantity.value = 0
                            amount.value = price.value.toDouble()

                            acteId.value = it?.id?.value
                        }
                    })
                } else if (items.wasRemoved()) {
                    orderItemsTotalProperty.set(0.0)
                    items.removed.forEach { m ->
                        orderItems.removeIf { it.acteId.value == m.id.value }
                    }
                }
            }
        }
    }

}