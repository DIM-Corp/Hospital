package com.example.demo.data.db

import com.example.demo.data.model.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

private var LOG_TO_CONSOLE: Boolean = false

fun newTransaction(): Transaction = TransactionManager.currentOrNew(Connection.TRANSACTION_SERIALIZABLE).apply {
    if (LOG_TO_CONSOLE) addLogger(StdOutSqlLogger)
}

fun enableConsoleLogger() {
    LOG_TO_CONSOLE = true
}

fun createTables() {
    with(newTransaction()) {
        SchemaUtils.create(
                ActesTbl,
                CashiersTbl,
                CashierOrdersTbl,
                Doctors,
                DoctorOrders,
                MedicalStaffsTbl,
                MedicationsTbl,
                OrdersTbl,
                OrderItemsTbl,
                PatientsTbl,
                ServicesTbl,
                SpecialitiesTbl,
                SynthesisSectionsTbl,
                UsersTbl
        )
    }
}

fun <T> execute(command: () -> T): T {
    with(newTransaction()) {
        return command().apply {
            commit()
            close()
        }
    }
}