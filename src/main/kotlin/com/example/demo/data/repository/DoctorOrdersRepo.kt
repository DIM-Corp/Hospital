package com.example.demo.data.repository

import com.example.demo.data.model.DoctorOrderModel
import com.example.demo.data.model.DoctorOrdersTbl
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import java.util.*

class DoctorOrdersRepo : CrudRepository<DoctorOrderModel, Int> {

    override fun create(entry: DoctorOrderModel): DoctorOrderModel {
        TODO("Not yet implemented")
    }

    override fun update(vararg entries: DoctorOrderModel): Iterable<DoctorOrderModel> {
        TODO("Not yet implemented")
    }

    override fun delete(entry: DoctorOrderModel) = DoctorOrdersTbl.deleteWhere { DoctorOrdersTbl.Order eq UUID.fromString(entry.order.value) }

    override fun find(id: Int): Iterable<DoctorOrderModel?> {
        TODO("Not yet implemented")
    }

    override fun findAll(): Iterable<DoctorOrderModel> {
        TODO("Not yet implemented")
    }

    override fun deleteAll() = DoctorOrdersTbl.deleteAll()
}