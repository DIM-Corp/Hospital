package com.example.demo.controller

import com.example.demo.data.db.SqlRepository
import com.example.demo.data.db.execute
import com.example.demo.data.model.*
import com.example.demo.utils.toMillis
import javafx.collections.ObservableList
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import tornadofx.*

class PatientController : Controller() {

    private val patientSqlRepository by lazy { SqlRepository(PatientsTbl, Patient) }
    private val userSqlRepository by lazy { SqlRepository(UsersTbl, User) }

    private val listOfPatients: ObservableList<PatientModel> = execute {
        PatientsTbl.join(UsersTbl, JoinType.LEFT, PatientsTbl.id, UsersTbl.id)
                .selectAll().map {
                    PatientModel().apply {
                        item = it.toPatientEntry()
                    }
                }
    }.observable()

    var items: ObservableList<PatientModel> by singleAssign()

    init {
        items = listOfPatients
    }

    fun add(newPatient: PatientModel): Int? {
        val newEntry = userSqlRepository.transactionNew {
            name = newPatient.name.value
            address = newPatient.address.value
            gender = newPatient.gender.value
            dateOfBirth = newPatient.age.value.toMillis()
            telephone = newPatient.telephone.value
        }

        return patientSqlRepository.transactionInsert {
            it[id] = newEntry!![UsersTbl.id]
            it[Condition] = newPatient.condition.value.toInt()
        }.value.also {
            listOfPatients.add(PatientModel().apply {
                item = PatientEntry(it, newPatient.condition.value.toInt(), newEntry!!.toUserEntry())
            })
        }
    }

    fun update(updatedItem: PatientModel): Int? {
        userSqlRepository.transactionSingleUpdate(updatedItem.id.value.toInt()) {
            it[Name] = updatedItem.name.value
            it[Address] = updatedItem.address.value
            it[Gender] = updatedItem.gender.value
            it[DateOfBirth] = updatedItem.age.value.toMillis()
            it[Telephone] = updatedItem.telephone.value
        }
        listOfPatients.find { it.id.value == updatedItem.id.value }?.apply {
            name.value = updatedItem.name.value
            address.value = updatedItem.address.value
            gender.value = updatedItem.gender.value
            age.value = updatedItem.age.value
            telephone.value = updatedItem.telephone.value
            condition.value = updatedItem.condition.value
        }
        return patientSqlRepository.transactionSingleUpdate(updatedItem.id.value.toInt()) {
            it[Condition] = updatedItem.condition.value.toInt()
        }
    }

    fun delete(patientItem: PatientEntry) = patientSqlRepository.deleteById(patientItem.id)

    fun select(id: Int) = patientSqlRepository.transactionSelectOne(id) {
        PatientModel().apply { item = it?.toPatientEntry() }
    }
}