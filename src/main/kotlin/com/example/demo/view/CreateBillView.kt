package com.example.demo.view

import com.example.demo.controller.UserController
import com.example.demo.data.model.UserViewModel
import com.example.demo.utils.capitalizeWords
import com.example.demo.utils.defaultPadding
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.layout.Priority
import tornadofx.*

class CreateBillView : View("My View") {

    private var toUpdateUser = false
    private val userModel = UserViewModel()
    private val userController: UserController by inject()
    lateinit var nameField: ComboBox<String>

    override val root = gridpane {

        paddingAll = defaultPadding

        row {
            form {
                vbox {
                    fieldset("Patient information", labelPosition = Orientation.HORIZONTAL) {
                        vbox {
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
                        }
                        flowpane {
                            hgap = defaultPadding
                            field(messages["age"]) {
                                spinner(1, 200, 20) {
                                    bind(userModel.age)
                                    promptText = messages["placeHolderAge"]
                                    isEditable = true
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
                    }
                }
                separator(orientation = Orientation.HORIZONTAL) { paddingBottom = 16.0 }
                tableview<UserViewModel> {
                    items = userController.items

                    column(messages["name"], UserViewModel::name)
                    column(messages["address"], UserViewModel::address)
                    column(messages["age"], UserViewModel::age)
                    column(messages["tel"], UserViewModel::telephone)
                }
                /*
                 * Left Pane Properties
                 */
                gridpaneColumnConstraints {
                    percentWidth = 40.0
                }
            }
            vbox {
                hbox {
                    spacing = defaultPadding
                    textfield {
                        promptText = messages["search"]
                        hgrow = Priority.ALWAYS
                    }
                    button(messages["search"])
                }
                tableview<UserViewModel> {
                    items = userController.items

                    column(messages["name"], UserViewModel::name)
                    column(messages["address"], UserViewModel::address)
                    column(messages["age"], UserViewModel::age)
                    column(messages["tel"], UserViewModel::telephone)
                }
                /*
                 * Right Pane Properties
                 */
                paddingLeft = defaultPadding
                spacing = defaultPadding
                gridpaneColumnConstraints { percentWidth = 60.0 }
            }
        }

        row {
            buttonbar {
                button(messages["clear"])
                button(messages["validate"]) {
                    isDefaultButton = true
                    action {
                        createPatient()
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
                else selectionModel.clearSelection()

                if (items.size == 1) {
                    selectionModel.clearAndSelect(0)
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