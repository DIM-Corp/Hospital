package com.example.demo.view

import com.example.demo.app.Styles
import com.example.demo.controller.OrderController
import com.example.demo.controller.OrderItemsController
import com.example.demo.data.model.OrderItemModel
import com.example.demo.data.model.OrderViewModel
import com.example.demo.utils.defaultPadding
import com.example.demo.utils.formatCurrencyCM
import com.example.demo.utils.fr_CM
import com.example.demo.utils.pattern_dateTime
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*

class OrdersView : View("Orders") {

    private val orderController: OrderController by inject()
    private val orderItemsController: OrderItemsController by inject()

    private var tableOrderItems by singleAssign<TableView<OrderItemModel>>()
    private var tableOrders by singleAssign<TableView<OrderViewModel>>()

    override val root = splitpane {
        vbox {
            label("Order history").addClass(Styles.subheading)
            tableOrders = tableview(orderController.items) {
                vgrow = Priority.ALWAYS

                smartResize()

                column("Order ID", OrderViewModel::id).prefWidth(50.0)
                column("Date", OrderViewModel::date) {
                    cellFormat { this.text = this.item.toString(pattern_dateTime, fr_CM) }
                }
                column("Client Name", OrderViewModel::patientName)

                selectionModel.selectedItemProperty().addListener { _, _, new ->
                    if (selectionModel.selectedItem != null) {
                        orderItemsController.loadOrderItemsForOrder(new.id.value)
                        tableOrderItems.smartResize()
                    } else orderItemsController.orderItems.clear()
                }
            }
            spacing = defaultPadding
            paddingAll = defaultPadding
        }
        vbox {
            label("Order Items").addClass(Styles.subheading)
            tableOrderItems = tableview(orderItemsController.orderItems) {

                vgrow = Priority.ALWAYS

                column(messages["label"], OrderItemModel::label).remainingWidth()
                column(messages["price"], OrderItemModel::price) {
                    cellFormat { this.text = this.item.formatCurrencyCM() }
                }.prefWidth(100.0)
                column(messages["qty"], OrderItemModel::quantity).maxWidth(96.0)
                column(messages["amount"], OrderItemModel::amount).cellFormat { this.text = this.item.formatCurrencyCM() }
            }
            spacing = defaultPadding
            paddingAll = defaultPadding
        }
    }


    init {
        workspace.tabContainer.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            tableOrders.selectionModel.clearSelection()
        }
    }
}
