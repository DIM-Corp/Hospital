package com.example.demo.utils

import java.time.*

val isDarkTheme = true

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

fun Long.toLocalDateTime(): LocalDateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime.toTimestamp(): Long = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun LocalDate.toTimestamp(): Long = this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toAge(): Int = Period.between(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).years

fun Int.toMillis(): Long = LocalDate.of(this, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()