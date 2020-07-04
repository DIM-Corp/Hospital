package com.example.demo.app

import com.example.demo.data.db.createTables
import com.example.demo.data.db.enableConsoleLogger
import com.example.demo.utils.isDarkTheme
import com.google.inject.Guice
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import org.jetbrains.exposed.sql.Database
import tornadofx.*
import kotlin.reflect.KClass

class MyApp : App(HospitalWorkspace::class, Styles::class) {

    init {
        val guice = Guice.createInjector(RepositoryModule())

        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>) = guice.getInstance(type.java)
        }
        // Initialize DB
        enableConsoleLogger()
        Database.connect("jdbc:sqlite:./app-hospital.db", "org.sqlite.JDBC")
        createTables()
    }

    override fun start(stage: Stage) {
        with(stage) {
            width = 1366.0
            height = 768.0
            minWidth = width / 1.9
            minHeight = height / 1.3
           // isMaximized = true
        }
        super.start(stage)
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        JMetro(view.root, if (isDarkTheme) Style.DARK else Style.LIGHT).apply {
            isAutomaticallyColorPanes = true
        }
        view.root.addClass(Styles.j_metro)
    }
}