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

    private val patientSqlRepository by lazy { SqlRepository(PatientsTbl, PatientTbl) }
    private val userSqlRepository by lazy { SqlRepository(UsersTbl, UserTbl) }

    private val listOfPatients: ObservableList<PatientEntryModel> = execute {
        ActesTbl.join(PatientsTbl, JoinType.LEFT, PatientsTbl.id, UsersTbl.id)
                .selectAll().map {
                    PatientEntryModel().apply {
                        item = it.toPatientEntry()
                    }
                }
    }.observable()

    var items: ObservableList<PatientEntryModel> by singleAssign()

    init {
        items = listOfPatients
    }

    fun add(
            newName: String,
            newAddress: String?,
            newGender: Boolean,
            newAge: Int,
            newTelephone: String,
            newCondition: Int
    ): Int? {
        val newEntry = userSqlRepository.transactionNew {
            name = newName
            address = newAddress
            gender = newGender
            dateOfBirth = newAge.toMillis()
            telephone = newTelephone
        }

        return patientSqlRepository.transactionInsert {
            it[id] = newEntry!![UsersTbl.id]
            it[Condition] = newCondition
        }.value.also {
            listOfPatients.add(PatientEntryModel().apply {
                item = PatientEntry(it, newCondition, newEntry!!.toUserEntry())
            })
        }
    }

    /*fun update(updatedItem: PatientViewModel): Int? {
        return patientSqlRepository.transactionSingleUpdate(updatedItem.id.value.toInt()) {
            it[Condition] = updatedItem.condition.value
        }
    }*/

    fun delete(patientItem: PatientEntry) = patientSqlRepository.deleteById(patientItem.id)

    fun select(id: Int) = patientSqlRepository.transactionSelectOne(id) {
        PatientEntryModel().apply { item = it?.toPatientEntry() }
    }
}