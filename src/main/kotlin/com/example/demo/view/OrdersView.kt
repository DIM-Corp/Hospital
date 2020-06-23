package com.example.demo.view

import com.example.demo.controller.OrderController
import com.example.demo.controller.OrderItemsController
import com.example.demo.data.model.OrderItemModel
import com.example.demo.data.model.OrderViewModel
import com.example.demo.utils.formatCurrencyCM
import javafx.scene.control.TableView
import tornadofx.*

class OrdersView : View("Orders") {

    private val orderController: OrderController by inject()
    private val orderItemsController: OrderItemsController by inject()

    private var tableOrderItems by singleAssign<TableView<OrderItemModel>>()
    private var tableOrders by singleAssign<TableView<OrderViewModel>>()

    override val root = splitpane {
        vbox {
            tableOrders = tableview(orderController.items) {
                column("Order ID", OrderViewModel::id)
                column("Date", OrderViewModel::date)
                column("Client Name", OrderViewModel::patientName)

                smartResize()

                selectionModel.selectedItemProperty().addListener { _, _, new ->
                    if (selectionModel.selectedItem != null) {
                        orderItemsController.loadOrderItemsForOrder(new.id.value)
                        tableOrderItems.smartResize()
                    } else orderItemsController.orderItems.clear()
                }
            }
        }
        vbox {
            tableOrderItems = tableview(orderItemsController.orderItems) {
                column(messages["label"], OrderItemModel::label).remainingWidth()
                column(messages["price"], OrderItemModel::price) {
                    cellFormat { this.text = this.item.formatCurrencyCM() }
                }.prefWidth(100.0)
                column(messages["qty"], OrderItemModel::quantity).maxWidth(96.0)
                column(messages["amount"], OrderItemModel::amtCalc).cellFormat { this.text = this.item.formatCurrencyCM() }
            }
        }
        workspace.tabContainer.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            tableOrders.selectionModel.clearSelection()
        }
    }
}
