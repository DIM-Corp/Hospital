package com.example.demo.controller

import com.example.demo.data.db.SqlRepository
import com.example.demo.data.model.*
import org.jetbrains.exposed.dao.id.EntityID
import tornadofx.*

class PatientController : Controller() {

    private val patientSqlRepository by lazy { SqlRepository(PatientsTbl, PatientTbl) }

    fun add(
            newId: Int,
            newCondition: Int
    ): Int? {
        return patientSqlRepository.transactionInsert {
            it[id] = EntityID(newId, PatientsTbl)
            it[Condition] = newCondition
        }.value
    }

    /*fun update(updatedItem: PatientViewModel): Int? {
        return patientSqlRepository.transactionSingleUpdate(updatedItem.id.value.toInt()) {
            it[Condition] = updatedItem.condition.value
        }
    }*/

    fun delete(patientItem: PatientEntry) = patientSqlRepository.deleteById(patientItem.id)

    fun select(id: Int) = patientSqlRepository.transactionSelectOne(id) {
        PatientViewModel().apply { item = it?.toPatientEntry() }
    }
}