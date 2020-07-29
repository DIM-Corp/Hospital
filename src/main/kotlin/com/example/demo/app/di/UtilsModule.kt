package com.example.demo.app.di

import com.example.demo.app.service.permission.PermissionHandler
import com.google.inject.AbstractModule
import com.google.inject.Provides

class UtilsModule : AbstractModule() {

    companion object {

        @Provides
        fun providePermissionHandler() = PermissionHandler()
    }
}