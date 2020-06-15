package com.example.demo.controller

import com.example.demo.data.db.SqlRepository
import com.example.demo.data.db.execute
import com.example.demo.data.model.*
import javafx.collections.ObservableList
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import tornadofx.*

class ActesController : Controller() {

    private val userSqlRepository by lazy { SqlRepository(ActesTbl, ActeTbl) }

    private val listOfActes: ObservableList<MedicationEntryModel> = execute {
        ActesTbl.join(MedicationsTbl, JoinType.LEFT, ActesTbl.id, MedicationsTbl.id)
                .join(SynthesisSectionsTbl, JoinType.LEFT, ActesTbl.SynthesisSection, SynthesisSectionsTbl.id)
                .selectAll().map {
                    MedicationEntryModel().apply {
                        item = it.toMedicationEntry()
                    }
                }
    }.observable()

    var items: ObservableList<MedicationEntryModel> by singleAssign()

    init {
        items = listOfActes
    }

    fun add(
            newName: String,
            newAppliedAmount: Double,
            newOfficialAmount: Double,
            synthesisSectionId: Int
    ): ActeEntry? {
        val newEntry = userSqlRepository.transactionNew {
            name = newName
            appliedAmount = newAppliedAmount.toBigDecimal()
            officialAmount = newOfficialAmount.toBigDecimal()
            synthesisSection = EntityID(synthesisSectionId, ActesTbl)
        }
        return newEntry?.toActeEntry()
    }

    /*fun update(updatedItem: ActeViewModel): Int? {
        return userSqlRepository.transactionSingleUpdate(updatedItem.id.value.toInt()) {
            it[Name] = updatedItem.name.value
            it[AppliedAmount] = updatedItem.appliedAmount.value.toBigDecimal()
            it[OfficialAmount] = updatedItem.officialAmount.value.toBigDecimal()
            it[SynthesisSection] = EntityID(updatedItem.synthesisSection.value.id, this)
        }
    }*/

    fun delete(acteEntry: ActeEntry) = userSqlRepository.deleteById(acteEntry.id)
}