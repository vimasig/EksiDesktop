package io.github.vimasig.eksidesktop.ui

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.lang.NullPointerException

class App: Application() {
    override fun start(stage: Stage?) {
        val fxmlLoader = FXMLLoader()
//        val controller = fxmlLoader.getController<MenuController>()
        val root = fxmlLoader.load<Parent>(App::class.java.getResource("/assets/fxml/index.fxml")?.openStream() ?: throw NullPointerException("Cannot find index.fxml"))
        val scene = Scene(root)
        stage?.let {
            it.scene = scene
            it.show()
            it.title = "EksiDesktop"
        }
    }
}