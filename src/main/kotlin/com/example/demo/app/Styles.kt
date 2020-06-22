package com.example.demo.app

import com.example.demo.utils.isDarkTheme
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val j_metro by cssclass()
        val accent_color by cssproperty<Color>("accent_color")
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 26.px
            fontWeight = FontWeight.BOLD
        }

        tabPane {
            backgroundColor = multi(Paint.valueOf(if (isDarkTheme) "#252525" else "#f3f3f3"))
        }

        j_metro and root {
            accent_color.value = Color.BLUEVIOLET
        }
    }
}