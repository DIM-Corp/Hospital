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
object Orders : IntIdTable() {
    override val id = integer("ActeId").entityId()
    val Timestamp = long("Timestamp")
    val Patient = reference("PatientID", Patients, fkName = "FK_Order_Patient")
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class Order(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Order>(Orders)

    var timeStamp by Orders.Timestamp
    var patient by Orders.Patient

    var orderItems by ActeTbl via OrderItems
}