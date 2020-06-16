package com.example.demo.view

import com.example.demo.controller.ActesController
import com.example.demo.controller.OrderController
import com.example.demo.controller.UserController
import com.example.demo.data.model.MedicationEntryModel
import com.example.demo.data.model.OrderItemModel
import com.example.demo.data.model.UserViewModel
import com.example.demo.utils.capitalizeWords
import com.example.demo.utils.defaultPadding
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TableView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*
import java.text.NumberFormat
import java.util.*


class CreateBillView : View("Create bill") {

    private var toUpdateUser = false
    private val userModel = UserViewModel()

    private val userController: UserController by inject()
    private val actesController: ActesController by inject()
    private val orderController: OrderController by inject()

    var nameField: ComboBox<String> by singleAssign()
    var tableOfActes: TableViewEditModel<MedicationEntryModel> by singleAssign()

    var tableOfCommandItems: TableView<OrderItemModel> by singleAssign()

    override val root = gridpane {

        paddingAll = defaultPadding

        row {
            vbox {
                form {
                    fieldset("Patient information", labelPosition = Orientation.HORIZONTAL) {
                        field(messages["name"]) {
                            nameField = combobox {
                                bind(userModel.name)
                                items = userController.items.map { it.name.value }.observable()
                                promptText = messages["placeHolderName"]
                                isEditable = true
                                fitToParentWidth()
                            }
                            addListenersAndValidation()
                        }
                        flowpane {
                            hgap = defaultPadding
                            field(messages["age"]) {
                                spinner(1, 200, 20) {
                                    bind(userModel.age)
                                    promptText = messages["placeHolderAge"]
                                }
                            }
                            field(messages["sex"]) {
                                togglegroup {
                                    bind(userModel.gender)
                                    alignment = Pos.BASELINE_LEFT
                                    radiobutton(text = messages["male"], value = true) { isSelected = true }
                                    radiobutton(text = messages["female"], value = false)
                                }
                            }
                            field(messages["address"]) {
                                textfield(userModel.address) { promptText = messages["placeHolderAddress"] }
                            }
                            field(messages["tel"]) {
                                textfield(userModel.telephone) {
                                    promptText = messages["placeHolderTel"]
                                    required()
                                    filterInput { it.controlNewText.isNotEmpty() && it.controlNewText.first() == '6' && it.controlNewText.length <= 9 }
                                }
                            }
                            field(messages["situation"]) {
                                togglegroup {
                                    radiobutton(text = messages["sitIn"], value = true) { isSelected = true }
                                    radiobutton(text = messages["sitOut"], value = false)
                                }
                            }
                        }
                        padding = Insets(0.0, 8.0, 0.0, 8.0)
                    }
                    paddingBottom = 0.0
                }
                separator(orientation = Orientation.HORIZONTAL)
                tableOfCommandItems = tableview {
                    items = orderController.orderItems

                    columnResizePolicy = SmartResize.POLICY

                    column("", OrderItemModel::acteId) {

                        maxWidth = 50.0
                        useMaxWidth = true

                        cellFormat {
                            graphic = group {
                                circle(0, 0, 8) { fill = Color.valueOf("#E21B1B") }
                                line(-2, 2, 2, -2) {
                                    stroke = Color.WHITE
                                    strokeWidth = 3.0
                                }
                                line(2, 2, -2, -2) {
                                    stroke = Color.WHITE
                                    strokeWidth = 3.0
                                }
                                addEventFilter(MouseEvent.MOUSE_CLICKED) { _ ->
                                    orderController.selectedItems.removeIf { it.id.value == item.toInt() }
                                }
                            }
                        }
                    }


                    column(messages["label"], OrderItemModel::label)
                    column(messages["price"], OrderItemModel::price).cellFormat {
                        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("", "CM"))
                        this.text = currencyFormat.format(this.item)
                    }
                    column(messages["qty"], OrderItemModel::quantity) {
                        cellFormat {
                            graphic = spinner(1, 999, item) {
                                bind(rowItem.qtyTemp)
                            }
                        }
                    }
                    column(messages["amount"], OrderItemModel::amtCalc).cellFormat {
                        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("", "CM"))
                        this.text = currencyFormat.format(this.item)
                    }
                }
                /*
                 * Left Pane Properties
                 */
                spacing = defaultPadding
                gridpaneColumnConstraints { percentWidth = 40.0 }
                fitToParentSize()
            }
            vbox {
                hbox {
                    textfield {
                        promptText = messages["search"]
                        hgrow = Priority.ALWAYS

                        textProperty().addListener { _, _, new ->
                            tableOfActes.tableView.selectionModel.clearSelection()
                            tableOfActes.tableView.items = actesController.items.filter { it.name.value.contains(new, true) }.observable()
                        }
                    }
                    button(messages["search"])
                }

                tableview<MedicationEntryModel> {
                    items = actesController.items
                    vgrow = Priority.ALWAYS

                    tableOfActes = editModel

                    setColumnResizePolicy { true }

                    column(messages["id"], MedicationEntryModel::id)
                    column(messages["label"], MedicationEntryModel::name)
                    column(messages["price"], MedicationEntryModel::officialAmount) {
                        cellFormat {
                            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("", "CM"))
                            this.text = currencyFormat.format(this.item)
                        }
                    }
                    column(messages["section"], MedicationEntryModel::synthesisSectionName)

                    selectionModel.selectedItemProperty().addListener { _, _, new ->
                        if (selectionModel.selectedItem != null) {
                            orderController.selectedItems.add(new)
                            tableOfCommandItems.requestResize()
                        }
                    }
/*
                    setRowFactory {
                        val row = TableRow<MedicationEntryModel>()
                        row.disableWhen(Bindings.selectInteger(row.itemProperty(), MedicationEntryModel::warehouseStock.name).lessThan(-1))
                        return@setRowFactory row
                    }*/
                }
                /*
                 * Right Pane Properties
                 */
                paddingLeft = defaultPadding
                spacing = defaultPadding
                gridpaneColumnConstraints { percentWidth = 60.0 }
                fitToParentSize()
            }
        }

        row {
            buttonbar {
                button(messages["clear"]) {
                    action {
                        userModel.rollback()
                    }
                }
                button(messages["validate"]) {
                    isDefaultButton = true
                    enableWhen(userModel.valid)
                    action {
                        userModel.commit {
                            createPatient()
                            userModel.rollback()
                        }
                    }
                }
                paddingTop = defaultPadding
            }
        }
    }

    private fun addListenersAndValidation() {
        with(nameField) {
            required()
            validator {
                val value = userModel.name.value
                if (!value.isNullOrBlank() && value.length < 3) error(messages["ld3"]) else null
            }

            selectionModel?.selectedIndexProperty()?.addListener { _, _, new ->
                if (nameField.items.isNotEmpty() && new.toInt() >= 0) {
                    toUpdateUser = true
                    val user = userController.items.find {
                        items[new.toInt()].contains(it!!.name.value)
                    }!!
                    with(userModel) {
                        address.value = user.address.value
                        gender.value = user.gender.value
                        age.value = user.age.value
                        telephone.value = user.telephone.value
                        editor.text = user.name.value
                    }
                }
            }

            editor.textProperty().addListener { _, _, new ->
                val filtered = userController.items.map { it.name.value }.filter {
                    it.contains(new, true)
                }

                items.removeIf { !filtered.contains(it) }
                filtered.forEach {
                    if (!items.contains(it)) items.add(it)
                }

                editor.text = new.capitalizeWords()

                hide()
                if (items.isNotEmpty()) show()
                else {
                    selectionModel.clearSelection()
                    hide()
                }
            }
        }
    }

    private fun createPatient() {
        userController.add(
                userModel.name.value,
                userModel.address.value,
                userModel.gender.value,
                userModel.age.value,
                userModel.telephone.value
        )
    }
}