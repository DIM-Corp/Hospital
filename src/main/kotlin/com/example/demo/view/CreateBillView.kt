package com.example.demo.view

import com.example.demo.utils.defaultPadding
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.*

class CreateBillView : View("My View") {

    override val root = gridpane {

        paddingAll = defaultPadding

        row {
            vbox {
                form {
                    fieldset("Patient information", labelPosition = Orientation.HORIZONTAL) {
                        field(messages["name"]) {
                            combobox<Person> {
                                promptText = messages["placeHolderName"]
                                isEditable = true
                                fitToParentWidth()
                            }
                        }
                        hbox {
                            spacing = defaultPadding
                            field(messages["age"]) {
                                textfield {
                                    promptText = messages["placeHolderAge"]
                                    filterInput { it.controlNewText.isInt() && it.controlNewText.toInt() < 200 }
                                }
                            }
                            field(messages["sex"]) {
                                togglegroup {
                                    alignment = Pos.BASELINE_LEFT
                                    radiobutton(text = messages["male"]) { isSelected = true }
                                    radiobutton(text = messages["female"])
                                }
                            }
                        }
                        hbox {
                            spacing = defaultPadding
                            field(messages["address"]) {
                                textfield { promptText = messages["placeHolderAddress"] }
                            }
                            field(messages["tel"]) {
                                textfield {
                                    promptText = messages["placeHolderTel"]
                                    filterInput { it.controlNewText.isNotEmpty() && it.controlNewText.first() == '6' && it.controlNewText.length <= 9 }
                                }
                            }
                        }
                        field(messages["situation"]) {
                            togglegroup {
                                radiobutton(text = messages["sitIn"]) { isSelected = true }
                                radiobutton(text = messages["sitOut"])
                            }
                        }
                    }
                }
                separator(orientation = Orientation.HORIZONTAL) { paddingBottom = 16.0 }
                tableview<Person> {
                    items = listOf(
                            Person("Joe Thompson", 33),
                            Person("Sam Smith", 29),
                            Person("Nancy Reams", 41)
                    ).observable()

                    column(messages["name"], Person::nameProperty)
                    column(messages["age"], Person::ageProperty)
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
