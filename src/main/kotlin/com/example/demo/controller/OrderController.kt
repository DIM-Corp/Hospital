package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.OrderModel
import com.example.demo.data.repository.OrderRepo
import com.example.demo.utils.isBetween
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import tornadofx.*
import java.time.LocalDate

class OrderController : Controller() {

    private val orderRepo: OrderRepo by di()

    private val listOfOrders: ObservableList<OrderModel> = execute { orderRepo.findAll() }.observable()

    private var filteredOrders = FilteredList<OrderModel>(listOfOrders)

    var items: ObservableList<OrderModel> by singleAssign()

    val searchProperty = SimpleStringProperty("")
    val startDateProperty = SimpleObjectProperty(LocalDate.now().minusMonths(1))
    val endDateProperty = SimpleObjectProperty(LocalDate.now())

    init {
        listOfOrders.sortByDescending { it.date.value }
        items = filteredOrders
        searchProperty.addListener { _, _, _ -> filter() }
    }

    fun addOrder(order: OrderModel) {
        listOfOrders.add(order)
        listOfOrders.sortByDescending { it.date.value }
    }

    fun filter() {
        filteredOrders.setPredicate { order ->
            val isBetweenSelectedDates = LocalDate.from(LocalDate.of(
                    order.date.value.year,
                    order.date.value.monthOfYear,
                    order.date.value.dayOfMonth
            )).isBetween(startDateProperty.value, endDateProperty.value)

            return@setPredicate when {
                searchProperty.value.toString().isEmpty() && isBetweenSelectedDates -> true
                order.id.value.contains(searchProperty.value.toString(), true) && isBetweenSelectedDates -> true
                order.patientName.value.contains(searchProperty.value.toString(), true) && isBetweenSelectedDates -> true
                else -> false
            }
        }
    }

    fun clearFilter() {
        startDateProperty.value = LocalDate.now().minusMonths(1)
        endDateProperty.value = LocalDate.now()
        filter()
    }
}