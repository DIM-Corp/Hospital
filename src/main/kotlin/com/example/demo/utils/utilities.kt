package com.example.demo.utils

import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import tornadofx.*
import java.text.NumberFormat
import java.time.*
import java.util.*

const val isDarkTheme = true

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

fun Long.toLocalDateTime(): LocalDateTime = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime.toTimestamp(): Long = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

fun LocalDate.toTimestamp(): Long = this.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Long.toAge(): Int = Period.between(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).years

fun Int.toMillis(): Long = LocalDate.of(LocalDate.now().year - this, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun Number.formatCurrencyCM(): String {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("", "CM"))
    return currencyFormat.format(this)
}

fun Node.cancelButton(event: () -> Unit): Node = group {
    circle(0, 0, 8) { fill = Color.valueOf("#E21B1B") }
    line(-2, 2, 2, -2) {
        stroke = Color.WHITE
        strokeWidth = 3.0
    }
    line(2, 2, -2, -2) {
        stroke = Color.WHITE
        strokeWidth = 3.0
    }
    addEventFilter(MouseEvent.MOUSE_CLICKED) { event.invoke() }
}