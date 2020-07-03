package com.example.demo.view

import com.example.demo.app.Styles
import com.example.demo.controller.ActesController
import com.example.demo.data.model.ActeModel
import com.example.demo.data.model.MedicationModel
import com.example.demo.utils.defaultPadding
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Orientation
import tornadofx.*

class ActesView : View("Actes/Medications") {

    private val actesModel = ActeModel()
    private val medicationModel = MedicationModel()

    private val isMedication = SimpleBooleanProperty(false)

    private val actesController: ActesController by inject()

    override val root = splitpane {

        vbox {
            form {
                fieldset("Acte/Medication", labelPosition = Orientation.HORIZONTAL) {
                    field(messages["name"]) {
                        textfield(actesModel.name) {
                            medicationModel.name.bind(actesModel.name)
                            required()
                        }
                    }
                    field("Official amount") {
                        textfield(actesModel.officialAmount) {
                            medicationModel.officialAmount.bind(actesModel.officialAmount)
                            required()
                        }
                    }
                    field("Applied amount") {
                        textfield(actesModel.appliedAmount) {
                            medicationModel.appliedAmount.bind(actesModel.appliedAmount)
                            required()
                        }
                    }
                    hbox {
                        spacing = defaultPadding
                        field("Section") {
                            combobox<String>(actesModel.synthesisSectionName) {
                                medicationModel.synthesisSectionName.bind(actesModel.synthesisSectionName)
                                items = actesController.sections.map { it.name.value }.observable()
                                isMedication.addListener { _, _, isTrue ->
                                    if (isTrue) selectionModel.select(0)
                                }
                                disableProperty().bind(isMedication)
                                required()
                            }
                        }
                        field {
                            checkbox("Medication") {
                                bind(isMedication)
                                prefWidth = 250.0
                            }
                        }
                    }
                    field("Counter Stock") {
                        textfield(medicationModel.counterStock) {
                            required()
                            disableProperty().bind(Bindings.not(isMedication))
                        }
                    }
                    field("Warehouse Stock") {
                        textfield(medicationModel.warehouseStock) {
                            required()
                            disableProperty().bind(Bindings.not(isMedication))
                        }
                    }
                    buttonbar {
                        button("Clear") {
                            action {
                                actesModel.rollback()
                                medicationModel.warehouseStock.value = 0
                                medicationModel.counterStock.value = 0
                            }
                        }
                        button("Delete") {
                            enableWhen(actesModel.valid)
                            action {

                            }
                        }
                        button("Save") {
                            isDefaultButton = true
                            enableWhen(actesModel.valid.and(Bindings.not(isMedication))
                                    .or(medicationModel.valid.and(isMedication).and(actesModel.valid))
                            )
                            action {
                                if (isMedication.value) actesController.addMedication(medicationModel)
                                else actesController.addActe(actesModel)
                                actesModel.rollback()
                                medicationModel.warehouseStock.value = 0
                                medicationModel.counterStock.value = 0
                            }
                        }
                    }
                }
            }
            paddingAll = defaultPadding
        }

        vbox {
            label("Actes").addClass(Styles.subheading)
            tableview<ActeModel> {
                items = actesController.actes
                column(messages["id"], ActeModel::id)
                column(messages["label"], ActeModel::name)
                column(messages["price"], ActeModel::appliedAmount)
                column(messages["price"], ActeModel::officialAmount)
            }
            label("Medications").addClass(Styles.subheading)
            tableview<MedicationModel> {
                items = actesController.medications
                column(messages["id"], MedicationModel::id)
                column(messages["label"], MedicationModel::name)
                column(messages["price"], MedicationModel::appliedAmount)
                column(messages["price"], MedicationModel::officialAmount)

                column(messages["price"], MedicationModel::counterStock)
                column(messages["price"], MedicationModel::warehouseStock)
            }
            spacing = defaultPadding
            paddingAll = defaultPadding
        }

        setDividerPositions(0.4)
    }
}
