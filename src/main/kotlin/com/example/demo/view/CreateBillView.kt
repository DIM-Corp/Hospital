package com.example.demo.view

import com.example.demo.data.db.transactionInsert
import com.example.demo.data.model.UserTbl
import com.example.demo.utils.defaultPadding
import com.example.demo.utils.toTimestamp
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Spinner
import javafx.scene.layout.Priority
import tornadofx.*
import java.time.LocalDate

class CreateBillView : View("My View") {

    init {
        transactionInsert(UserTbl) {
            name = "Johnson"
            surname = "Doe"
            address = "Efoulan Lac"
            gender = true
            dateOfBirth = LocalDate.now().toTimestamp()
            telephone = "454545"
        }
    }

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
                        flowpane {
                            hgap = defaultPadding
                            field(messages["age"]) {
                                spinner(1, 200, 20) {
                                    promptText = messages["placeHolderAge"]
                                    addClass(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL)
                                }
                            }
                            field(messages["sex"]) {
                                togglegroup {
                                    alignment = Pos.BASELINE_LEFT
                                    radiobutton(text = messages["male"]) { isSelected = true }
                                    radiobutton(text = messages["female"])
                                }
                            }
                            field(messages["address"]) {
                                textfield { promptText = messages["placeHolderAddress"] }
                            }
                            field(messages["tel"]) {
                                textfield {
                                    promptText = messages["placeHolderTel"]
                                    filterInput { it.controlNewText.isNotEmpty() && it.controlNewText.first() == '6' && it.controlNewText.length <= 9 }
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

                    addClass("alternating-row-colors")
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
