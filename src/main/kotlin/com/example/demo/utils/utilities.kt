package com.example.demo.utils

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }