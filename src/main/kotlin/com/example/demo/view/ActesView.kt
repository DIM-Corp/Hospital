package com.example.demo.view

import com.example.demo.controller.ActesController
import com.example.demo.data.model.ActeViewModel
import com.example.demo.utils.defaultPadding
import com.example.demo.utils.isDarkTheme
import javafx.geometry.Orientation
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.layout.Priority
import jfxtras.styles.jmetro.FlatDialog
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import tornadofx.*

class ActesView : View("Actes/Medications") {

    private val actesModel = ActeViewModel()
    private val actesController: ActesController by inject()

    override val root = vbox {
        label("Hello") { }

        button("Test") {
            action {
                FlatDialog<String>().apply {
                    title = "Acte/medication"

                    dialogPane.buttonTypes.add(ButtonType("Save", ButtonBar.ButtonData.OK_DONE))
                    dialogPane.buttonTypes.add(ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE))

                    dialogPane.prefWidth = 400.0

                    dialogPane.content = form {
                        fieldset("Acte/Medication", labelPosition = Orientation.HORIZONTAL) {
                            field(messages["name"]) {
                                textfield { }
                            }
                            field("Official amount") {
                                textfield { }
                            }
                            field("Applied amount") {
                                textfield { }
                            }
                            hbox {
                                spacing = defaultPadding
                                field("Section") {
                                    hgrow = Priority.ALWAYS
                                    combobox<String> {
                                        fitToParentWidth()
                                    }
                                }
                                field {
                                    checkbox("Medication") {
                                        paddingTop = 3.0
                                    }
                                }
                            }
                        }
                    }

                    JMetro(dialogPane, if (isDarkTheme) Style.DARK else Style.LIGHT)
                }.show()
            }
        }


    }
}
