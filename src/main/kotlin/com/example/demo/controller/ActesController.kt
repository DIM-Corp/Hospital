package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.MedicationModel
import com.example.demo.data.model.toActeModel
import com.example.demo.data.model.toMedicationModel
import com.example.demo.data.repository.ActesRepo
import com.example.demo.data.repository.MedicationRepo
import javafx.collections.ObservableList
import tornadofx.*

class ActesController : Controller() {

    private val actesRepo: ActesRepo by di()
    private val medicationRepo: MedicationRepo by di()

    private val listOfActes: ObservableList<MedicationModel> = execute {
        val result = mutableListOf<MedicationModel>()
        result.addAll(medicationRepo.findAll())
        result.addAll(actesRepo.findAll().map { it.toMedicationModel() })
        result
    }.observable()

    var items: ObservableList<MedicationModel> by singleAssign()

    init {
        items = listOfActes
    }

    fun addMedication(newMedication: MedicationModel) {
        execute {
            newMedication.id.value = actesRepo.create(newMedication.toActeModel()).id.value
            medicationRepo.create(newMedication)
            listOfActes.add(newMedication)
        }
    }
}