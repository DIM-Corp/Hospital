package com.example.demo.app

import com.example.demo.controller.StateController
import com.example.demo.data.db.createTables
import com.example.demo.data.db.enableConsoleLogger
import com.example.demo.utils.isDarkTheme
import com.example.demo.view.LoginView
import com.google.inject.Guice
import javafx.scene.Parent
import javafx.stage.Stage
import jfxtras.styles.jmetro.JMetro
import jfxtras.styles.jmetro.Style
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import tornadofx.*
import kotlin.reflect.KClass

class MyApp : App(LoginView::class, Styles::class) {

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

    private val stateController by inject<StateController>()

    private fun observeLogInState() {
        stateController.isLoggedIn.addListener { _, _, isLoggedIn ->

            val view = if (isLoggedIn) find<HospitalWorkspace>() else find<LoginView>()
            bindJMetro(view.root)

            view.whenDocked {
                runAsync {
                    GlobalScope.launch { delay(700) }
                } ui {
                    view.currentStage?.isMaximized = isLoggedIn
                }
            }

            stateController.currentWindow?.hide()
            view.openWindow(escapeClosesWindow = false)
            stateController.currentWindow = view.currentWindow
        }
    }

    override fun start(stage: Stage) {
        observeLogInState()
        with(stage) {
            width = 1366.0
            height = 768.0
            minWidth = width / 1.9
            minHeight = height / 1.3
        }
        super.start(stage)
    }

    override fun onBeforeShow(view: UIComponent) {
        super.onBeforeShow(view)
        view.preferences {
            stateController.isLoggedIn.value = getBoolean("isLoggedIn", false)
        }
        stateController.currentWindow = view.currentWindow
        bindJMetro(view.root)
    }

    private fun bindJMetro(parent: Parent) {
        JMetro(parent, if (isDarkTheme) Style.DARK else Style.LIGHT).apply {
            isAutomaticallyColorPanes = true
        }
        parent.addClass(Styles.j_metro)
    }
}