package com.example.demo.data.db

import javafx.collections.ObservableList
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateStatement
import tornadofx.*

open class SqlRepository<ID : Comparable<ID>, TBL : IdTable<ID>, T : EntityClass<ID, M>, M : Entity<ID>>(
        private var table: TBL,
        private var row: T
) {

    /**
     * FieldSet used by getter executions.
     * This may be overridden to add table joins.
     */
    open fun retrieverFields(): FieldSet = table

    fun deleteById(id: ID): Unit = execute {
        table.deleteWhere { table.id eq id }
    }

    fun count(): Long = execute {
        table.selectAll().count()
    }

    fun <VM : ViewModel> transactionSelectAll(mapping: (Query) -> ObservableList<VM>) = execute {
        mapping(table.selectAll())
    }

    fun transactionInsert(body: M.() -> Unit) = execute {
        try {
            row.new(body).readValues
        } catch (e: Exception) {
            println("Error $e")
            null
        }
    }

    fun transactionSingleUpdate(key: ID, body: TBL.(UpdateStatement) -> Unit) = execute {
        if (table.select { table.id eq key }.count() == 0L) return@execute null
        table.update({ table.id eq key }, body = body)
    }

}