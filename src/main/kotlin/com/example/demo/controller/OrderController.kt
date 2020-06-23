package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.*
import com.example.demo.utils.Item
import com.example.demo.utils.PrinterService
import javafx.collections.ObservableList
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.insert
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import tornadofx.*
import java.util.*

class OrderController : Controller() {

    var selectedItems: ObservableList<MedicationEntryModel> by singleAssign()
    var orderItems: ObservableList<OrderItemModel> by singleAssign()
    private var printerService: PrinterService by singleAssign()

    fun createOrder(patientEntryModel: PatientEntryModel) = execute {
        val o = OrderTbl.new(UUID.randomUUID()) {
            timeStamp = LocalDate.now().toDateTime(LocalTime.now())
            patient = EntityID(patientEntryModel.id.value.toInt(), PatientsTbl)
        }
        orderItems.forEach { item ->
            OrderItemsTbl.insert {
                it[Acte] = EntityID(item.acteId.value.toInt(), ActesTbl)
                it[Order] = o.id
                it[Quantity] = item.qtyTemp.value.toInt()
            }
        }
    }

    fun printOrder(patientEntryModel: PatientEntryModel) {
        createOrder(patientEntryModel)
        printerService.printReceipt(
                orderItems.map { Item(it.label.value, it.qtyTemp.value, it.price.value.toDouble()) },
                patientEntryModel.name.value
        )
    }

    init {
        printerService = PrinterService()

        orderItems = mutableListOf<OrderItemModel>().observable()
        selectedItems = mutableListOf<MedicationEntryModel>().observable()
        selectedItems.onChange { items ->
            while (items.next()) {
                if (items.wasAdded()) {
                    orderItems.addAll(items.addedSubList.map {
                        OrderItemModel().apply {
                            label.value = it?.name?.value
                            price.value = it?.appliedAmount?.value
                            quantity.value = 0
                            amtCalc.value = price.value.toDouble()

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