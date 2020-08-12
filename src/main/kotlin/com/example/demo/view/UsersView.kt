package com.example.demo.view

import com.example.demo.controller.MedicalStaffController
import com.example.demo.data.model.MedicalStaffModel
import com.example.demo.data.model.ServiceModel
import com.example.demo.utils.defaultPadding
import javafx.geometry.Orientation
import javafx.scene.control.ComboBox
import tornadofx.*


class UsersView : View() {

    private var toUpdateStaff = false

    private val staffModel = MedicalStaffModel()
    private val serviceModel = ServiceModel()

    private val staffController: MedicalStaffController by inject()

    var servicesCombo: ComboBox<String> by singleAssign()

    override val root = splitpane {

        title = messages["staff"]

        vbox {
            form {
                fieldset("Basic staff information") {
                    field(messages["name"]) {
                        textfield(staffModel.name) {
                            promptText = messages["placeHolderName"]
                            required()
                        }
                    }
                    field(messages["address"]) {
                        textfield(staffModel.address) { promptText = messages["placeHolderAddress"] }
                    }
                    field(messages["tel"]) {
                        textfield(staffModel.telephone) {
                            promptText = messages["placeHolderTel"]
                            required()
                            filterInput { it.controlNewText.isNotEmpty() && it.controlNewText.first() == '6' && it.controlNewText.length <= 9 }
                        }
                    }
                    field(messages["age"]) {
                        spinner(1, 200, 20) {
                            bind(staffModel.age)
                            isEditable = true
                        }
                    }
                    field(messages["sex"]) {
                        togglegroup {
                            bind(staffModel.gender)
                            radiobutton(text = messages["male"], value = true) { isSelected = true }
                            radiobutton(text = messages["female"], value = false)
                        }
                    }
                    field(messages["service"]) {
                        servicesCombo = combobox(staffModel.serviceName) {
                            items = staffController.servicesItems.map { it.name.value }.observable()
                        }
                    }
                    buttonbar {
                        button(messages["save"]) {
                            isDefaultButton = true
                            enableWhen(staffModel.valid)
                            action {
                                staffModel.commit {
                                    if (!toUpdateStaff) staffController.add(staffModel)
                                    else staffController.update(staffModel)
                                    staffModel.rollback()
                                }
                            }
                        }
                    }
                }
            }

            separator(orientation = Orientation.HORIZONTAL)

            form {
                fieldset("Hospital staff") {
                    tableview<MedicalStaffModel> {
                        items = staffController.staffItems
                        column(messages["username"], MedicalStaffModel::username)
                        column(messages["name"], MedicalStaffModel::name)
                        column(messages["service"], MedicalStaffModel::serviceName)
                        column(messages["telephone"], MedicalStaffModel::telephone)
                    }
                    buttonbar {
                        button(messages["save"]) {
                            isDefaultButton = true
                        }
                        paddingTop = defaultPadding
                    }
                }
            }

            spacing = defaultPadding
            paddingAll = defaultPadding
        }

        vbox {

            spacing = defaultPadding
            paddingAll = defaultPadding
        }

        vbox {

            form {
                fieldset(messages["services"]) {

                    field(messages["service"]) {
                        textfield(serviceModel.name) {
                            promptText = messages["service"]
                            required()
                        }
                    }

                    tableview<ServiceModel> {
                        items = staffController.servicesItems
                        column(messages["name"], ServiceModel::name)

                        selectionModel.selectedIndexProperty().addListener { _, _, newValue ->
                            serviceModel.item = staffController.servicesItems[newValue.toInt()].item
                        }
                    }
                    buttonbar {
                        button(messages["delete"]) {
                            action {
                                staffController.deleteService(serviceModel)
                                serviceModel.rollback()
                            }
                        }
                        button(messages["save"]) {
                            enableWhen(serviceModel.valid)
                            isDefaultButton = true
                            action {
                                staffController.addService(serviceModel)
                                serviceModel.rollback()
                            }
                        }
                        paddingTop = defaultPadding
                    }
                }
            }

            spacing = defaultPadding
            paddingAll = defaultPadding
        }

        setDividerPositions(0.3, 0.95)
    }
}
