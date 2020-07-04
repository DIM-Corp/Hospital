package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.*
import com.example.demo.data.repository.ActesRepo
import com.example.demo.data.repository.MedicationRepo
import com.example.demo.data.repository.SectionRepo
import javafx.collections.ObservableList
import tornadofx.*

class ActesController : Controller() {

    private val actesRepo: ActesRepo by di()
    private val medicationRepo: MedicationRepo by di()
    private val sectionRepo: SectionRepo by di()

    private val listOfActes: ObservableList<MedicationModel> = execute {
        val result = mutableListOf<MedicationModel>()
        result.addAll(medicationRepo.findAll())
        result.addAll(actesRepo.findAll().map { it.toMedicationModel() })
        result
    }.observable()

    private val actesOnly: ObservableList<ActeModel> = execute {
        actesRepo.findAll()
    }.observable()

    private val medicationsOnly: ObservableList<MedicationModel> = execute {
        medicationRepo.findAll()
    }.observable()

    private val listOfSections: ObservableList<SynthesisSectionModel> = execute {
        sectionRepo.findAll()
    }.observable()

    var items: ObservableList<MedicationModel> by singleAssign()

    var actes: ObservableList<ActeModel> by singleAssign()
    var medications: ObservableList<MedicationModel> by singleAssign()
    var sections: ObservableList<SynthesisSectionModel> by singleAssign()

    init {
        items = listOfActes
        actes = actesOnly
        medications = medicationsOnly
        sections = listOfSections
    }

    fun updateActe(updatedMedication: MedicationModel, isMedicationOld: Boolean, isMedicationNew: Boolean) {
        execute {
            val medicationModel = updatedMedication.copy()
            when {
                isMedicationOld and isMedicationNew -> {
                    actesRepo.update(medicationModel.toActeModel())
                    medicationRepo.update(medicationModel)

                    replace(medicationModel, medicationsOnly)
                }
                !isMedicationOld and !isMedicationNew -> {
                    actesRepo.update(medicationModel.toActeModel())

                    actesOnly.removeIf { it.id.value.toInt() == medicationModel.id.value.toInt() }
                    actesOnly.add(medicationModel.toActeModel())

                    replace(medicationModel.toActeModel(), actesOnly)
                }
                isMedicationOld and !isMedicationNew -> {
                    medicationRepo.delete(medicationModel)
                    actesRepo.update(medicationModel.toActeModel())

                    medicationsOnly.removeIf { it.id.value.toInt() == medicationModel.id.value.toInt() }
                    actesOnly.add(medicationModel.toActeModel())
                }
                else -> {
                    medicationRepo.create(medicationModel)
                    actesRepo.update(medicationModel.toActeModel())

                    actesOnly.removeIf { it.id.value.toInt() == medicationModel.id.value.toInt() }
                    medicationsOnly.add(medicationModel)
                }
            }
            replace(medicationModel, listOfActes)
        }
    }

    fun addMedication(newMedication: MedicationModel) {
        execute {
            newMedication.synthesisSectionId.value = sections.find {
                it.name.value == newMedication.synthesisSectionName.value
            }!!.id.value
            newMedication.id.value = actesRepo.create(newMedication.toActeModel(true)).id.value
            medicationRepo.create(newMedication)
            items.add(newMedication)
            medications.add(newMedication)
        }
    }

    fun addActe(newActe: ActeModel) {
        execute {
            newActe.synthesisSectionId.value = sections.find {
                it.name.value == newActe.synthesisSectionName.value
            }!!.id.value
            newActe.id.value = actesRepo.create(newActe).id.value
            items.add(newActe.toMedicationModel())
            actes.add(newActe)
        }
    }

    fun deleteActe(actesModel: ActeModel) {
        execute {
            actesRepo.delete(actesModel)
            medicationRepo.delete(actesModel.toMedicationModel())
            items.removeIf { it.id.value == actesModel.id.value }
            actes.removeIf { it.id.value == actesModel.id.value }
            medications.removeIf { it.id.value == actesModel.id.value }
        }
    }

    private fun <T> replace(newItem: T, list: ObservableList<T>) {
        val item = list.find {
            when (it) {
                is MedicationModel -> it.id.value.toInt() == (newItem as MedicationModel).id.value.toInt()
                is ActeModel -> it.id.value.toInt() == (newItem as ActeModel).id.value.toInt()
                else -> false
            }
        }
        val index = list.indexOf(item)
        list[index] = newItem
    }
}