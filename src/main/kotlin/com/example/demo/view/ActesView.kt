package com.example.demo.view

import com.example.demo.app.Styles
import com.example.demo.controller.ActesController
import com.example.demo.data.model.ActeModel
import com.example.demo.data.model.MedicationModel
import com.example.demo.data.model.toMedicationModel
import com.example.demo.utils.defaultPadding
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Orientation
import javafx.scene.control.ComboBox
import javafx.scene.control.TableView
import tornadofx.*

class ActesView : View("Actes/Medications") {

    private val actesModel = ActeModel()
    private val medicationModel = MedicationModel()

    private val isMedication = SimpleBooleanProperty(false)

    private val actesController: ActesController by inject()

    var tableOfActes: TableView<ActeModel> by singleAssign()
    var tableOfMedications: TableView<MedicationModel> by singleAssign()
    var sectionsCombo: ComboBox<String> by singleAssign()

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
                            sectionsCombo = combobox(actesModel.synthesisSectionName) {
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
                            checkbox("Medication", isMedication) {
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
                                tableOfActes.selectionModel.clearSelection()
                                tableOfMedications.selectionModel.clearSelection()
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
            tableOfActes = tableview {
                items = actesController.actes
                column(messages["id"], ActeModel::id)
                column(messages["label"], ActeModel::name)
                column(messages["price"], ActeModel::appliedAmount)
                column(messages["price"], ActeModel::officialAmount)
                selectionModel.selectedItemProperty().addListener { _, _, it ->
                    if (selectionModel.selectedItem != null) {
                        updateActeModel(it.toMedicationModel())
                        isMedication.value = false
                    }
                }
            }
            label("Medications").addClass(Styles.subheading)
            tableOfMedications = tableview {
                items = actesController.medications
                column(messages["id"], MedicationModel::id)
                column(messages["label"], MedicationModel::name)
                column(messages["price"], MedicationModel::appliedAmount)
                column(messages["price"], MedicationModel::officialAmount)

                column(messages["price"], MedicationModel::counterStock)
                column(messages["price"], MedicationModel::warehouseStock)

                selectionModel.selectedItemProperty().addListener { _, _, it ->
                    if (selectionModel.selectedItem != null) {
                        updateActeModel(it)
                        isMedication.value = true
                        medicationModel.counterStock.value = it.counterStock.value
                        medicationModel.warehouseStock.value = it.warehouseStock.value
                    }
                }
            }
            spacing = defaultPadding
            paddingAll = defaultPadding
        }

        setDividerPositions(0.4)
    }

    private fun updateActeModel(it: MedicationModel) {
        actesModel.id.value = it.id.value
        actesModel.name.value = it.name.value
        actesModel.officialAmount.value = it.officialAmount.value
        actesModel.appliedAmount.value = it.appliedAmount.value
        actesModel.synthesisSectionId.value = it.synthesisSectionId.value
        actesModel.synthesisSectionName.value = it.synthesisSectionName.value
        sectionsCombo.selectionModel.select(it.synthesisSectionName.value)
    }
}
