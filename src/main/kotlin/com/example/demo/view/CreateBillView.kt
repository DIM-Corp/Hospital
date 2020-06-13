package com.example.demo.view

import com.example.demo.controller.UserController
import com.example.demo.data.model.UserViewModel
import com.example.demo.utils.defaultPadding
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.*

class CreateBillView : View("My View") {

    private val userModel = UserViewModel()
    private val controller: UserController by inject()

    override val root = gridpane {

        paddingAll = defaultPadding

        row {
            vbox {
                form {
                    fieldset("Patient information", labelPosition = Orientation.HORIZONTAL) {
                        vbox {
                            field(messages["name"]) {
                                combobox<UserViewModel> {
                                    promptText = messages["placeHolderName"]
                                    isEditable = true
                                    fitToParentWidth()
                                }
                            }
                            field(messages["surname"]) {
                                textfield(userModel.surname) {
                                    promptText = messages["placeHolderSurname"]
                                    isEditable = true
                                    fitToParentWidth()
                                }
                            }
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
                    items = controller.items

                    column(messages["name"], UserViewModel::name)
                    column(messages["surname"], UserViewModel::surname)
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
                tableview<Person> {
                    items = listOf(
                            Person("Joe Thompson", 33),
                            Person("Sam Smith", 29),
                            Person("Nancy Reams", 41)
                    ).observable()

                    column(messages["name"], Person::nameProperty)
                    column(messages["age"], Person::ageProperty)

                    vboxConstraints {
                        vGrow = Priority.ALWAYS
                    }
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
                button(messages["validate"]) { isDefaultButton = true }
                paddingTop = defaultPadding
            }
        }
    }
}

class Person(name: String, age: Int) {
    var nameProperty: String by SimpleStringProperty(name)
    var ageProperty by SimpleIntegerProperty(age)
}
