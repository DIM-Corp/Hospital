package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.PatientModel
import com.example.demo.data.model.toUserModel
import com.example.demo.data.repository.PatientRepo
import com.example.demo.data.repository.UserRepo
import javafx.collections.ObservableList
import tornadofx.*

class PatientController : Controller() {

    private val patientRepo: PatientRepo by di()
    private val userRepo: UserRepo by di()

    private val listOfPatients: ObservableList<PatientModel> = execute { patientRepo.findAll() }.observable()

    var items: ObservableList<PatientModel> by singleAssign()

    init {
        items = listOfPatients
    }

    fun add(newPatient: PatientModel) {
        execute {
            userRepo.create(newPatient.toUserModel())
            listOfPatients.add(patientRepo.create(newPatient))
        }
    }

    fun update(updatedItem: PatientModel) {
        execute {
            userRepo.update(updatedItem.toUserModel())
            val result = patientRepo.update(updatedItem).first()

            listOfPatients.find { it.id.value == updatedItem.id.value }?.apply {
                name.value = result.name.value
                address.value = result.address.value
                gender.value = result.gender.value
                age.value = result.age.value
                telephone.value = result.telephone.value
                condition.value = result.condition.value
            }
        }
    }

    fun delete(patientItem: PatientModel) = execute { patientRepo.delete(patientItem) }

    fun select(id: Int) = execute { patientRepo.find(id) }
}