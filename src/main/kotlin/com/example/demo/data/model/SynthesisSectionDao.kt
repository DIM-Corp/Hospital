@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object SynthesisSections : IntIdTable() {
    override val id = integer("ActeId").entityId()
    val Name = varchar("Name", 64)
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class SynthesisSection(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<SynthesisSection>(SynthesisSections)

    var name by SynthesisSections.Name
}