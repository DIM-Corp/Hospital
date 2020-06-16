package com.example.demo.controller

import com.example.demo.data.model.MedicationEntryModel
import com.example.demo.data.model.OrderItemModel
import javafx.collections.ObservableList
import tornadofx.*

class OrderController : Controller() {

    var selectedItems: ObservableList<MedicationEntryModel> by singleAssign()
    var orderItems: ObservableList<OrderItemModel> by singleAssign()

    init {
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