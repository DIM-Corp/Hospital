package com.example.demo.controller

import com.example.demo.data.db.execute
import com.example.demo.data.model.MedicalStaffModel
import com.example.demo.data.model.ServiceModel
import com.example.demo.data.model.toUserModel
import com.example.demo.data.repository.MedicalStaffRepo
import com.example.demo.data.repository.ServicesRepo
import com.example.demo.data.repository.UserRepo
import javafx.collections.ObservableList
import tornadofx.*

class MedicalStaffController : Controller() {

    private val staffRepo: MedicalStaffRepo by di()
    private val userRepo: UserRepo by di()
    private val servicesRepo: ServicesRepo by di()

    private val listOfStaff: ObservableList<MedicalStaffModel> = execute { staffRepo.findAll() }.observable()
    private val listOfServices: ObservableList<ServiceModel> = execute { servicesRepo.findAll() }.observable()

    var staffItems: ObservableList<MedicalStaffModel> by singleAssign()
    var servicesItems: ObservableList<ServiceModel> by singleAssign()

    init {
        staffItems = listOfStaff
        servicesItems = listOfServices
    }

    fun add(newStaff: MedicalStaffModel) {
        execute {
            newStaff.username.value = "${newStaff.name.value.replace(" ", "").toLowerCase()}${newStaff.age.value}"
            newStaff.password.value = newStaff.username.value
            newStaff.serviceId.value = servicesItems.find { it.name.value == newStaff.serviceName.value }?.name?.value?.toInt()

            val result = userRepo.create(newStaff.toUserModel(false))
            newStaff.id.value = result.id.value
            staffItems.add(staffRepo.create(newStaff))
        }
    }

    fun update(updatedItem: MedicalStaffModel) {
        execute {
            userRepo.update(updatedItem.toUserModel())
            val result = staffRepo.update(updatedItem).first()

            listOfStaff.find { it.id.value == updatedItem.id.value }?.apply {
                name.value = result.name.value
                address.value = result.address.value
                gender.value = result.gender.value
                age.value = result.age.value
                telephone.value = result.telephone.value

                serviceId.value = result.serviceId.value
                serviceName.value = result.serviceName.value

                username.value = result.password.value
                password.value = result.password.value
                role.value = result.role.value
            }
        }
    }

    fun delete(staffItem: MedicalStaffModel) = execute { staffRepo.delete(staffItem) }

}