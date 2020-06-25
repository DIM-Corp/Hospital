package com.example.demo.view

import com.example.demo.app.Styles
import com.example.demo.controller.ActesController
import com.example.demo.controller.OrderItemsController
import com.example.demo.controller.PatientController
import com.example.demo.data.model.MedicationModel
import com.example.demo.data.model.OrderItemModel
import com.example.demo.data.model.PatientModel
import com.example.demo.utils.cancelButton
import com.example.demo.utils.capitalizeWords
import com.example.demo.utils.defaultPadding
import com.example.demo.utils.formatCurrencyCM
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*
import java.util.*


class CreateBillView : View("Create bill") {

    private var toUpdateUser = false
    private val patientEntryModel = PatientModel()

    private val patientController: PatientController by inject()
    private val actesController: ActesController by inject()
    private val orderItemsController: OrderItemsController by inject()

    var nameField: ComboBox<String> by singleAssign()
    var tableOfActes: TableViewEditModel<MedicationModel> by singleAssign()

    var tableOfCommandItems: TableView<OrderItemModel> by singleAssign()

    override val root = splitpane {
        vbox {
            form {
                fieldset("Patient information", labelPosition = Orientation.HORIZONTAL) {
                    field(messages["name"]) {
                        nameField = combobox {
                            bind(patientEntryModel.name)
                            items = patientController.items.map { it.name.value }.observable()
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
                                bind(patientEntryModel.age)
                                isEditable = true
                            }
                        }
                        field(messages["sex"]) {
                            togglegroup {
                                bind(patientEntryModel.gender)
                                alignment = Pos.BASELINE_LEFT
                                radiobutton(text = messages["male"], value = true) { isSelected = true }
                                radiobutton(text = messages["female"], value = false)
                            }
                        }
                        field(messages["address"]) {
                            textfield(patientEntryModel.address) { promptText = messages["placeHolderAddress"] }
                        }
                        field(messages["tel"]) {
                            textfield(patientEntryModel.telephone) {
                                promptText = messages["placeHolderTel"]
                                required()
                                filterInput { it.controlNewText.isNotEmpty() && it.controlNewText.first() == '6' && it.controlNewText.length <= 9 }
                            }
                        }
                        field(messages["situation"]) {
                            togglegroup {
                                bind(patientEntryModel.condition)
                                radiobutton(text = messages["sitIn"], value = 1) { isSelected = true }
                                radiobutton(text = messages["sitOut"], value = 0)
                            }
                        }
                    }
                    padding = Insets(0.0, 8.0, 0.0, 8.0)
                }
            }

            separator(orientation = Orientation.HORIZONTAL)

            tableOfCommandItems = tableview {
                items = orderItemsController.orderItems

                vgrow = Priority.ALWAYS

                column("", OrderItemModel::acteId) {
                    fixedWidth(30)
                    cellFormat {
                        graphic = cancelButton {
                            orderItemsController.selectedItems.removeIf { it.id.value == item.toInt() }
                            orderItemsController.updateOrderTotal()
                        }
                    }
                }
                column(messages["label"], OrderItemModel::label).remainingWidth()
                column(messages["price"], OrderItemModel::price) {
                    cellFormat { this.text = this.item.formatCurrencyCM() }
                }.prefWidth(100.0)
                column(messages["qty"], OrderItemModel::quantity) {
                    cellFormat {
                        graphic = spinner(1, 999, item) {
                            bind(rowItem.qtyTemp)
                            isEditable = true
                            this.valueProperty().addListener { _, o, n ->
                                if (o != n) orderItemsController.updateOrderTotal()
                            }
                        }
                    }
                }.maxWidth(96.0)
                column(messages["amount"], OrderItemModel::amount).cellFormat { this.text = this.item.formatCurrencyCM() }
            }

            label {
                bind(Bindings.format(Locale("fr", "CM"), "Total:        %,.0f FCFA", orderItemsController.orderItemsTotalProperty))
                addClass(Styles.heading)
            }

            buttonbar {
                button(messages["clear"]) {
                    action {
                        orderItemsController.selectedItems.clear()
                        orderItemsController.orderItemsTotalProperty.set(0.0)
                        patientEntryModel.rollback()
                        toUpdateUser = false
                    }
                }
                button(messages["validate"]) {
                    isDefaultButton = true
                    enableWhen(patientEntryModel.valid)
                    action {
                        patientEntryModel.commit {
                            if (!toUpdateUser) patientController.add(patientEntryModel)
                            else patientController.update(patientEntryModel)
                            orderItemsController.printOrder(patientEntryModel)
                            patientEntryModel.rollback()
                        }
                    }
                }
            }

            /*
             * Left Pane Properties
             */
            spacing = defaultPadding - 8
            paddingAll = defaultPadding
        }

        vbox {
            anchorpane {
                textfield {
                    promptText = messages["search"]
                    prefWidth = 600.0

                    textProperty().addListener { _, _, new ->
                        tableOfActes.tableView.selectionModel.clearSelection()
                        tableOfActes.tableView.items = actesController.items.filter { it.name.value.contains(new, true) }.observable()
                        tableOfActes.tableView.requestResize()
                    }

                    anchorpaneConstraints {
                        applyToNode(this@textfield)
                        this.leftAnchor = 0
                        this.rightAnchor = 0
                    }
                }
            }

            tableview<MedicationModel> {
                items = actesController.items
                tableOfActes = editModel
                vgrow = Priority.ALWAYS

                column(messages["id"], MedicationModel::id)
                column(messages["label"], MedicationModel::name)
                column(messages["price"], MedicationModel::appliedAmount) {
                    cellFormat { this.text = this.item.formatCurrencyCM() }
                }.prefWidth(100.0)
                column(messages["section"], MedicationModel::synthesisSectionName)

                selectionModel.selectedItemProperty().addListener { _, _, new ->
                    if (selectionModel.selectedItem != null && !orderItemsController.selectedItems.contains(new)) {
                        orderItemsController.selectedItems.add(new)
                        tableOfCommandItems.requestResize()
                        orderItemsController.updateOrderTotal()
                    }
                }
                smartResize()
            }
            /*
             * Right Pane Properties
             */
            spacing = defaultPadding
            paddingAll = defaultPadding
        }

        setDividerPositions(0.36)
    }

    private fun addListenersAndValidation() {
        with(nameField) {
            required()
            validator {
                val value = patientEntryModel.name.value
                if (!value.isNullOrBlank() && value.length < 3) error(messages["ld3"]) else null
            }

            selectionModel?.selectedIndexProperty()?.addListener { _, _, new ->
                if (nameField.items.isNotEmpty() && new.toInt() >= 0) {
                    toUpdateUser = true
                    val user = patientController.items.find {
                        items[new.toInt()].contains(it!!.name.value)
                    }!!
                    with(patientEntryModel) {
                        id.value = user.id.value
                        address.value = user.address.value
                        gender.value = user.gender.value
                        age.value = user.age.value
                        telephone.value = user.telephone.value
                        condition.value = user.condition.value
                        name.value = user.name.value
                    }
                }
            }

            editor.textProperty().addListener { _, _, new ->
                val filtered = patientController.items.map { it.name.value }.filter {
                    it?.contains(new, true) ?: false
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
}