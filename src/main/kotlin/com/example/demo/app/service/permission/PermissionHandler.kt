package com.example.demo.app.service.permission

import com.example.demo.data.db.execute
import com.example.demo.data.model.CashierModel
import com.example.demo.data.model.DoctorModel
import com.example.demo.data.model.MedicalStaffModel
import com.example.demo.data.repository.CashierRepo
import com.example.demo.data.repository.DoctorRepo
import com.example.demo.data.repository.MedicalStaffRepo
import com.google.inject.Inject
import tornadofx.*

class PermissionHandler {

    @Inject
    lateinit var medicalStaffRepo: MedicalStaffRepo

    @Inject
    lateinit var doctorRepo: DoctorRepo

    @Inject
    lateinit var cashierRepo: CashierRepo

    fun getPermissions(userId: Int): List<Permission> {
        val user = execute {
            val doctor = doctorRepo.find(userId).firstOrNull()
            val cashier = cashierRepo.find(userId).firstOrNull()
            val staff = medicalStaffRepo.find(userId).firstOrNull()
            when {
                doctor != null -> return@execute doctor
                cashier != null -> return@execute cashier
                staff != null -> return@execute staff
                else -> return@execute null
            }
        }
        return buildPermissions(user)
    }

    private fun buildPermissions(user: ItemViewModel<out Any>?): List<Permission> {
        val permissions = mutableListOf<Permission>()

        when (user) {
            is DoctorModel -> {
                // TODO: Add to permissions list
            }
            is CashierModel -> {
                // TODO: Add to permissions list
            }
            is MedicalStaffModel -> {
                // TODO: Add to permissions list
            }
        }

        return permissions
    }

    enum class Permission {
        // TODO: Create permission types
    }

}