package com.example.demo.app

import com.example.demo.app.di.RepositoryModule
import com.example.demo.app.di.UserModule
import com.example.demo.app.di.UtilsModule
import com.example.demo.controller.StateController
import com.example.demo.data.db.createTables
import com.example.demo.data.db.enableConsoleLogger
import com.example.demo.utils.isDarkTheme
import com.example.demo.view.LoginView
import com.google.inject.Guice
import javafx.scene.Parent
import javafx.stage.Screen
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
        val guice = Guice.createInjector(RepositoryModule(), UtilsModule(), UserModule())

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
        stateController.isLoggedIn.onChange { isLoggedIn ->

            val view = if (isLoggedIn) find<HospitalWorkspace>() else find<LoginView>()
            bindJMetro(view.root)

            view.preferences {
                putBoolean("isLoggedIn", isLoggedIn)
            }

            runAsync {
                GlobalScope.launch { delay(1000) }
            } ui {

                if (isLoggedIn) find<LoginView>().close()
                else find<HospitalWorkspace>().close()

                view.openWindow(escapeClosesWindow = false)?.apply {
                    width = if (!isLoggedIn) loginViewSize.first else Screen.getPrimary().bounds.width
                    height = if (!isLoggedIn) loginViewSize.second else Screen.getPrimary().bounds.height - 32
                    minWidth = width / 1.9
                    minHeight = height / 1.3
                    if (isLoggedIn) {
                        x = 0.0
                        y = 0.0
                    }
                    isMaximized = isLoggedIn
                }
            }
        }
    }

    private val loginViewSize: Pair<Double, Double> = Pair(512.0, 264.0)

    override fun start(stage: Stage) {
        observeLogInState()
        with(stage) {
            width = loginViewSize.first
            height = loginViewSize.second
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
        bindJMetro(view.root)
    }

    private fun bindJMetro(parent: Parent) {
        JMetro(parent, if (isDarkTheme) Style.DARK else Style.LIGHT).apply {
            isAutomaticallyColorPanes = true
        }
        parent.addClass(Styles.j_metro)
    }
}