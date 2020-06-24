package com.example.demo.app

import com.example.demo.data.repository.ActesRepo
import com.example.demo.data.repository.OrderItemsRepo
import com.example.demo.data.repository.OrderRepo
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

    }

}