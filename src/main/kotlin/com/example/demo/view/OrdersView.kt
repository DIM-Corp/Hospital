package com.example.demo.view

import com.example.demo.app.Styles
import com.example.demo.controller.OrderController
import com.example.demo.controller.OrderItemsController
import com.example.demo.data.model.OrderItemModel
import com.example.demo.data.model.OrderModel
import com.example.demo.data.model.getPatientModel
import com.example.demo.utils.defaultPadding
import com.example.demo.utils.formatCurrencyCM
import com.example.demo.utils.fr_CM
import com.example.demo.utils.pattern_dateTime
import javafx.beans.binding.Bindings
import javafx.geometry.Pos
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*
import java.util.*

class OrdersView : View() {

    private val orderController: OrderController by inject()
    private val orderItemsController: OrderItemsController by inject()

    private var tableOrderItems by singleAssign<TableView<OrderItemModel>>()
    private var tableOrders by singleAssign<TableView<OrderModel>>()

    override val root = splitpane {

        title = messages["orders"]

        vbox {
            label(messages["o_his"]).addClass(Styles.subheading)
            hbox {
                textfield(orderController.searchProperty) { promptText = messages["search"]; hgrow = Priority.ALWAYS }
                label(messages["between"]) { alignment = Pos.BASELINE_LEFT; fitToParentHeight() }
                datepicker(orderController.startDateProperty) { prefWidth = 100.0 }
                label(messages["and"]) { alignment = Pos.BASELINE_LEFT; fitToParentHeight() }
                datepicker(orderController.endDateProperty) { prefWidth = 100.0 }
                button(messages["filter"]).action { orderController.filter() }
                button(messages["clear"]).action { orderController.clearFilter() }
                spacing = defaultPadding - 12
            }
            tableOrders = tableview(orderController.items) {
                vgrow = Priority.ALWAYS

                smartResize()

                column(messages["oid"], OrderModel::id).prefWidth(50.0)
                column(messages["date"], OrderModel::date) {
                    cellFormat { this.text = this.item.toString(pattern_dateTime, fr_CM) }
                }
                column(messages["cname"], OrderModel::patientName)

                selectionModel.selectedItemProperty().addListener { _, _, new ->
                    if (selectionModel.selectedItem != null) {
                        orderItemsController.loadOrderItemsForOrder(new.id.value)
                        tableOrderItems.smartResize()
                    } else orderItemsController.orderItems.clear()
                }
            }

            buttonbar {
                button(messages["print"]) {
                    isDefaultButton = true
                    enableWhen(tableOrders.selectionModel.selectedIndexProperty().gt(-1))
                    action {
                        orderItemsController.printOrder(tableOrders.selectedItem!!.getPatientModel(), false)
                    }
                }
            }
            spacing = defaultPadding
            paddingAll = defaultPadding
        }
        vbox {
            label(messages["o_items"]).addClass(Styles.subheading)
            tableOrderItems = tableview(orderItemsController.orderItems) {

                vgrow = Priority.ALWAYS

                column(messages["label"], OrderItemModel::label).remainingWidth()
                column(messages["price"], OrderItemModel::price) {
                    cellFormat { this.text = this.item.formatCurrencyCM() }
                }.prefWidth(100.0)
                column(messages["qty"], OrderItemModel::quantity).maxWidth(96.0)
                column(messages["amount"], OrderItemModel::amount).cellFormat { this.text = this.item.formatCurrencyCM() }
            }

            label {
                bind(Bindings.format(Locale("fr", "CM"), "Total:        %,.0f FCFA", orderItemsController.orderItemsTotalProperty))
                addClass(Styles.heading)
            }

            spacing = defaultPadding
            paddingAll = defaultPadding
        }
    }


    init {
        workspace.tabContainer.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            tableOrders.selectionModel.clearSelection()
            orderItemsController.orderItemsTotalProperty.set(0.0)
        }
    }
}
