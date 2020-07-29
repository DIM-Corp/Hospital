package com.example.demo.app.di

import com.example.demo.data.repository.*
import com.google.inject.AbstractModule
import com.google.inject.Provides

class RepositoryModule : AbstractModule() {
    companion object {

        @Provides
        fun provideActesRepo() = ActesRepo()

        @Provides
        fun provideOrderRepo() = OrderRepo()

        @Provides
        fun provideOrderItemsRepo() = OrderItemsRepo()

        @Provides
        fun provideMedicationRepo() = MedicationRepo()

        @Provides
        fun providePatientRepo() = PatientRepo()

        @Provides
        fun provideUserRepo() = UserRepo()

        @Provides
        fun provideSectionRepo() = SectionRepo()

        @Provides
        fun provideMedicalStaffRepo() = MedicalStaffRepo()

        @Provides
        fun provideDoctorRepo() = DoctorRepo()

        @Provides
        fun provideCashierRepo() = CashierRepo()

    }

}