package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.*
import com.example.demo.utils.Item
import com.example.demo.utils.PrinterService
import javafx.collections.ObservableList
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import tornadofx.*
import java.util.*

class OrderItemsController : Controller() {

    var selectedItems: ObservableList<MedicationModel> by singleAssign()
    var orderItems: ObservableList<OrderItemModel> by singleAssign()
    private var printerService: PrinterService by singleAssign()

    private val orderController: OrderController by inject()

    fun loadOrderItemsForOrder(uuid: String) = execute {
        orderItems.clear()
        orderItems.addAll(
                OrderItemsTbl
                        .innerJoin(ActesTbl)
                        .innerJoin(OrdersTbl)
                        .select { OrderItemsTbl.Order eq UUID.fromString(uuid) }
                        .map { OrderItemModel().apply { item = it.toOrderItemEntry() } })
    }

    private fun createOrder(patientModel: PatientModel) = execute {
        val o = Order.new(UUID.randomUUID()) {
            timeStamp = LocalDate.now().toDateTime(LocalTime.now())
            patient = EntityID(patientModel.id.value.toInt(), PatientsTbl)
        }
        orderItems.forEach { item ->
            OrderItemsTbl.insert {
                it[Acte] = EntityID(item.acteId.value.toInt(), ActesTbl)
                it[Order] = o.id
                it[Quantity] = item.qtyTemp.value.toInt()
            }
        }
        // Update orders view
        orderController.addOrder(OrderModel().apply {
            item = OrderEntry(o.id.value.toString(), o.timeStamp.toLocalDateTime(), patientModel.item)
        })
        // Return a pair (UUID, timestamp)
        Pair(o.id.value.toString(), o.timeStamp)
    }

    fun printOrder(patientModel: PatientModel) {
        val pair = createOrder(patientModel)
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
                    items.removed.forEach { m ->
                        orderItems.removeIf { it.acteId.value == m.id.value }
                    }
                }
            }
        }
    }

}