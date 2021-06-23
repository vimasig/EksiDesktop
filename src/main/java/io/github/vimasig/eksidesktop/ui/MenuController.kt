package io.github.vimasig.eksidesktop.ui

import io.github.vimasig.eksidesktop.api.EksiBrowser
import io.github.vimasig.eksidesktop.api.EksiParser
import io.github.vimasig.eksidesktop.ui.handler.MenuControllerHandler
import io.github.vimasig.eksidesktop.ui.handler.impl.MainPageHandler
import io.github.vimasig.eksidesktop.ui.handler.impl.TopicListHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.layout.VBox
import org.fxmisc.richtext.StyleClassedTextArea
import kotlin.reflect.KClass

class MenuController {

    lateinit var parser: EksiParser

    // FX Items
    @FXML private lateinit var base: VBox
    @FXML private lateinit var refreshButton: MenuItem
    @FXML lateinit var mainPage: StyleClassedTextArea
    @FXML lateinit var topicList: VBox
    @FXML lateinit var status: Label
    @FXML lateinit var pageChoice: ChoiceBox<Int>

    // Page buttons
    @FXML lateinit var backwardButton: Button
    @FXML lateinit var forwardButton: Button

    val pageButtons = PageButtons()
    inner class PageButtons {
        fun setEnabled(b: Boolean) {
            backwardButton.isDisable = !b
            forwardButton.isDisable = !b
            pageChoice.isDisable = !b
        }
    }

    // Handlers
    @PublishedApi internal lateinit var handlers: List<MenuControllerHandler>

    fun initialize() {
        // TODO: Selectable themes
        base.stylesheets.add("/assets/css/dark/base.css")

        // Parser
        this.updateParser()

        // Handlers
        this.handlers = listOf(
            MainPageHandler(this),
            TopicListHandler(this)
        )
        this.handlers.forEach(MenuControllerHandler::init)

        // Refresh button
        this.refreshButton.setOnAction {
            if(this.getMenuControllerHandler(MainPageHandler::class).cacheManager.isRunning()) {
                System.err.println("Cannot load showcased topics. An update is already in progress.")
                return@setOnAction
            }

            this.updateParser()
            this.getMenuControllerHandler(MainPageHandler::class).loadShowcasedTopics()
            this.getMenuControllerHandler(TopicListHandler::class).update()
        }
    }

    inline fun <reified T : MenuControllerHandler> getMenuControllerHandler(clazz: KClass<T>): T {
        require(clazz != MenuControllerHandler::class) { "clazz parameter cannot be base class" }
        return handlers.stream()
            .filter { clazz.isInstance(it) }
            .findAny()
            .orElseThrow { NullPointerException("Cannot find handler " + clazz.qualifiedName) } as T
    }

    private fun updateParser() {
        val newDoc = EksiBrowser.getDocument()
        if(!this::parser.isInitialized) this.parser = EksiParser(newDoc)
        else this.parser.document = newDoc
    }
}