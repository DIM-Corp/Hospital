package com.example.demo.app

import com.example.demo.utils.HashingUtils
import com.google.inject.AbstractModule
import com.google.inject.Provides

class UtilsModule : AbstractModule() {

    companion object {

        @Provides
        fun provideHashingUtils() = HashingUtils()
    }
}