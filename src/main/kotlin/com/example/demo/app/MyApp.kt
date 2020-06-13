package com.example.demo.app

import com.example.demo.data.db.createTables
import com.example.demo.data.db.enableConsoleLogger
import com.example.demo.view.CreateBillView
import javafx.scene.paint.Color
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import org.jetbrains.exposed.sql.Database
import tornadofx.*

class MyApp : App(CreateBillView::class, Styles::class) {

    init {
        reloadStylesheetsOnFocus()
        reloadViewsOnFocus()
        // Initialize DB
        enableConsoleLogger()
        Database.connect("jdbc:sqlite:./app-hospital.db", "org.sqlite.JDBC")
        createTables()
    }

    override fun start(stage: Stage) {
        with(stage) {
            width = 1280.0
            height = 600.0
        }
        super.start(stage)
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        JMetro(view.root, Style.DARK).apply {
            isAutomaticallyColorPanes = true
        }
        view.root.style = "accent_color: ${Color.BLUEVIOLET.css}"
    }
}