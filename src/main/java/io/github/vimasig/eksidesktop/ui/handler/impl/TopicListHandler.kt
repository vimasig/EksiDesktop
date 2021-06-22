package io.github.vimasig.eksidesktop.ui.handler.impl

import io.github.vimasig.eksidesktop.ui.MenuController
import io.github.vimasig.eksidesktop.ui.handler.MenuControllerHandler
import javafx.geometry.Insets
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.layout.HBox

class TopicListHandler(menuController: MenuController) : MenuControllerHandler(menuController) {

    override fun init() {
        this.menuController.topicList.stylesheets.add("/assets/css/dark/topiclist.css")
        this.update()
    }

    fun update() {
        this.menuController.topicList.children.clear()

        Label("Gündem").let { // Trend label
            it.styleClass.add("title")
            it.padding = Insets(10.0, 0.0, 0.0, 10.0)
            this.menuController.topicList.children.add(it)
        }

        val ep = this.menuController.parser
        val topicList = ep.getTopicList()
        topicList.forEach { topic ->
            val hBox = HBox()
            hBox.cursor = Cursor.HAND
            hBox.setOnMousePressed {
                // Clear styles
                this.clearSelectedStyles()

                // HBox style
                hBox.requestFocus()
                hBox.styleClass.add("selected")

                // Navigate
                val mainPageHandler = this.menuController.getMenuControllerHandler(MainPageHandler::class)
                if (!this.menuController.mainPage.text.startsWith(topic.getTitle() + System.lineSeparator().repeat(3))) // Check if the page is already loaded
                    mainPageHandler.navigate(topic)
            }

            // Generate & add topic box
            Label(topic.getTitle()).let {
                it.padding = Insets(15.0, 10.0, 15.0, 10.0)
                it.isWrapText = true
                hBox.children.add(it)
            }

            this.menuController.topicList.children.add(hBox)
        }
    }

    private fun clearSelectedStyles() {
        this.menuController.topicList.children.stream()
            .filter { it is HBox }
            .forEach { it.styleClass.remove("selected") }
    }
}