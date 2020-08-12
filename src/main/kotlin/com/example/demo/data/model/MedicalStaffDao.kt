@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.example.demo.data.model

import com.example.demo.utils.HashingUtils
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import tornadofx.*

/**
 * @author forntoh
 * @version 1.0
 * @created 12-Jun-2020 11:17:28 AM
 */
object MedicalStaffsTbl : IdTable<Int>() {
    override val id = reference("Matriculation", UsersTbl)
    var Username = varchar("Username", 32)
    var Password = text("Password")
    var Service = reference("ServiceID", ServicesTbl, fkName = "FK_MedicalStaff_WorksIn").nullable()
    var Role = integer("Role").default(1)
    override val primaryKey = PrimaryKey(columns = *arrayOf(id))
}

class MedicalStaff(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<MedicalStaff>(MedicalStaffsTbl)

    var username by MedicalStaffsTbl.Username
    var password by MedicalStaffsTbl.Password
    var service by MedicalStaffsTbl.Service
    var role by MedicalStaffsTbl.Role
}

fun ResultRow.toMedicalStaffEntry(ignoreUser: Boolean = false, ignoreService: Boolean = true) = MedicalStaffEntry(
        this[MedicalStaffsTbl.id].value,
        this[MedicalStaffsTbl.Username],
        this[MedicalStaffsTbl.Password],
        this[MedicalStaffsTbl.Role],
        if (ignoreService) null else this.toServiceEntry(),
        if (ignoreUser) null else this.toUserEntry()
)

class MedicalStaffEntry(id: Int, username: String, password: String, role: Int, service: ServiceEntry?, user: UserEntry?) {
    var idProperty = SimpleIntegerProperty(id)
    val id by idProperty

    var usernameProperty = SimpleStringProperty(username)
    val username by usernameProperty

    var passwordProperty = SimpleStringProperty(password)
    val password by passwordProperty

    var roleProperty = SimpleIntegerProperty(role)
    val role by roleProperty

    val service = ServiceModel().apply { item = service }

    val user = UserModel().apply { item = user }

}

class MedicalStaffModel : ItemViewModel<MedicalStaffEntry>() {
    val id = bind { item?.idProperty }
    val username = bind { item?.usernameProperty }
    val password = bind { item?.passwordProperty }
    val role = bind { item?.roleProperty }

    val serviceId = bind { item?.service?.id }
    val serviceName = bind { item?.service?.name }

    val name = bind { item?.user?.name }
    val address = bind { item?.user?.address }
    val gender = bind { item?.user?.gender }
    val age = bind { item?.user?.age }
    val telephone = bind { item?.user?.telephone }
}

fun MedicalStaffModel.toRow(hashingUtils: HashingUtils): MedicalStaffsTbl.(UpdateBuilder<*>) -> Unit = {
    it[id] = EntityID(this@toRow.id.value.toInt(), MedicalStaffsTbl)

    it[Username] = this@toRow.username.value
    it[Password] = hashingUtils.hash(this@toRow.password.value)

    if (this@toRow.serviceId.value != null) it[Service] = EntityID(this@toRow.serviceId.value.toInt(), MedicalStaffsTbl)
    if (this@toRow.role.value != null) it[Role] = this@toRow.role.value.toInt()
}

fun MedicalStaffModel.toUserModel(hasId: Boolean = true): UserModel = UserModel().apply {
    item = UserEntry(
            if (hasId) this@toUserModel.id.value.toInt() else 0,
            this@toUserModel.name.value,
            this@toUserModel.address.value,
            this@toUserModel.gender.value,
            this@toUserModel.age.value.toInt(),
            this@toUserModel.telephone.value
    )
}