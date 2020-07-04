package com.example.demo.view

import com.example.demo.app.Styles
import com.example.demo.controller.ActesController
import com.example.demo.data.model.ActeModel
import com.example.demo.data.model.MedicationModel
import com.example.demo.data.model.toMedicationModel
import com.example.demo.utils.defaultPadding
import com.example.demo.utils.formatCurrencyCM
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
    private var isMedicationOld = false
    private var toUpdate = false

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
                    field(messages["o_amt"]) {
                        textfield(actesModel.officialAmount) {
                            medicationModel.officialAmount.bind(actesModel.officialAmount)
                            required()
                        }
                    }
                    field(messages["a_amt"]) {
                        textfield(actesModel.appliedAmount) {
                            medicationModel.appliedAmount.bind(actesModel.appliedAmount)
                            required()
                        }
                    }
                    hbox {
                        spacing = defaultPadding
                        field(messages["section"]) {
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
                            checkbox(messages["med"], isMedication) {
                                prefWidth = 250.0
                            }
                        }
                    }
                    field(messages["c_stock"]) {
                        textfield(medicationModel.counterStock) {
                            required()
                            disableProperty().bind(Bindings.not(isMedication))
                        }
                    }
                    field(messages["w_stock"]) {
                        textfield(medicationModel.warehouseStock) {
                            required()
                            disableProperty().bind(Bindings.not(isMedication))
                        }
                    }
                    buttonbar {
                        button(messages["clear"]) {
                            action {
                                actesModel.rollback()
                                medicationModel.warehouseStock.value = 0
                                medicationModel.counterStock.value = 0
                                tableOfActes.selectionModel.clearSelection()
                                tableOfMedications.selectionModel.clearSelection()
                            }
                        }
                        button(messages["delete"]) {
                            enableWhen(actesModel.valid)
                            action {
                                actesController.deleteActe(actesModel)
                                tableOfActes.selectionModel.clearSelection()
                                tableOfMedications.selectionModel.clearSelection()
                                toUpdate = false
                                actesModel.rollback()
                            }
                        }
                        button(messages["save"]) {
                            isDefaultButton = true
                            enableWhen(actesModel.valid.and(Bindings.not(isMedication))
                                    .or(medicationModel.valid.and(isMedication).and(actesModel.valid))
                            )
                            action {
                                if (toUpdate) {
                                    actesController.updateActe(medicationModel, isMedicationOld, isMedication.value)
                                } else {
                                    if (isMedication.value) actesController.addMedication(medicationModel)
                                    else actesController.addActe(actesModel)
                                }
                                toUpdate = false
                                actesModel.rollback()
                                medicationModel.warehouseStock.value = 0
                                medicationModel.counterStock.value = 0
                                tableOfActes.selectionModel.clearSelection()
                                tableOfMedications.selectionModel.clearSelection()
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
                smartResize()
                column(messages["id"], ActeModel::id)
                column(messages["label"], ActeModel::name).remainingWidth()
                column(messages["o_amt"], ActeModel::appliedAmount).cellFormat { this.text = this.item.formatCurrencyCM() }
                column(messages["a_amt"], ActeModel::officialAmount) {
                    cellFormat { this.text = this.item.formatCurrencyCM() }
                    contentWidth(padding = 50.0, useAsMin = true)
                }
                selectionModel.selectedItemProperty().addListener { _, _, it ->
                    if (selectionModel.selectedItem != null) {
                        updateActeModel(it.toMedicationModel())
                        isMedication.value = false
                        isMedicationOld = false
                        toUpdate = true
                    }
                }
            }
            label("Medications").addClass(Styles.subheading)
            tableOfMedications = tableview {
                items = actesController.medications
                smartResize()
                column(messages["id"], MedicationModel::id)
                column(messages["label"], MedicationModel::name).remainingWidth()
                column(messages["o_amt"], MedicationModel::appliedAmount).cellFormat { this.text = this.item.formatCurrencyCM() }
                column(messages["a_amt"], MedicationModel::officialAmount).cellFormat { this.text = this.item.formatCurrencyCM() }

                column(messages["c_stock"], MedicationModel::counterStock)
                column(messages["w_stock"], MedicationModel::warehouseStock)

                selectionModel.selectedItemProperty().addListener { _, _, it ->
                    if (selectionModel.selectedItem != null) {
                        updateActeModel(it)
                        isMedication.value = true
                        isMedicationOld = true
                        toUpdate = true
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
        medicationModel.id.value = it.id.value
        medicationModel.synthesisSectionId.value = it.synthesisSectionId.value
        sectionsCombo.selectionModel.select(it.synthesisSectionName.value)
    }
}
