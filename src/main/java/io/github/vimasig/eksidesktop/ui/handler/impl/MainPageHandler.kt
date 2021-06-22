package io.github.vimasig.eksidesktop.ui.handler.impl

import io.github.vimasig.eksidesktop.api.EksiCacheManager
import io.github.vimasig.eksidesktop.api.EksiTopic
import io.github.vimasig.eksidesktop.api.IEksiAuthor
import io.github.vimasig.eksidesktop.api.IEksiEntryItem
import io.github.vimasig.eksidesktop.ui.MenuController
import io.github.vimasig.eksidesktop.ui.handler.MenuControllerHandler
import io.github.vimasig.eksidesktop.ui.model.EksiMessage
import io.github.vimasig.eksidesktop.utils.DesktopUtils
import io.github.vimasig.eksidesktop.utils.StringUtils
import javafx.geometry.Insets
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import org.fxmisc.richtext.StyleClassedTextArea
import java.net.URL


private data class Hyperlink(val url: URL, val startIndex: Int, val endIndex: Int) {
    constructor(url: URL, author: IEksiAuthor, length: Int) : this(url, length - author.getName().length, length)
}

class MainPageHandler(menuController: MenuController) : MenuControllerHandler(menuController) {

    private val cacheManager = EksiCacheManager(this.menuController.parser, status = menuController.status)
    private val mainPage = this.menuController.mainPage
    private val hyperlinks = arrayListOf<Hyperlink>()
    private lateinit var latestTopic: EksiTopic

    private fun findHyperlink(charIndex: Int): Hyperlink? {
        for (hyperlink in hyperlinks) {
            if(hyperlink.startIndex <= charIndex && hyperlink.endIndex >= charIndex)
                return hyperlink
        }; return null
    }

    override fun init() {
        // Style
        this.mainPage.padding = Insets(15.0)

        // Theme
        this.mainPage.stylesheets.add("/assets/css/dark/mainpage.css")

        // Load first page
        this.loadShowcasedTopics()

        // Handle hyperlink clicks
        this.mainPage.setOnMouseClicked {
            val index = this.mainPage.caretPosition
            val styles = this.mainPage.getStyleAtPosition(index)
            if(styles.contains("link")) {
                val hyperlink = this.findHyperlink(index)
                if (hyperlink != null) {
                    if(!DesktopUtils.browseURL(hyperlink.url))
                        Alert(Alert.AlertType.ERROR, EksiMessage.CANNOT_OPEN_LINK + hyperlink.url, ButtonType.OK).let { it.headerText = ""; it.show() }
                } else error("Cannot find Hyperlink object of style \"link\" at index $index")
            }
        }

        // Handle forward button click
        this.menuController.backwardButton.setOnAction {
            this.latestTopic.backward()
            this.navigate(this.latestTopic)
        }
        this.menuController.forwardButton.setOnAction {
            this.latestTopic.forward(this.getPageCountByTopic(this.latestTopic))
            this.navigate(this.latestTopic)
        }
//        this.menuController.pageChoice.setOnAction {
//            this.latestTopic.page = this.menuController.pageChoice.value
//            this.navigate(this.latestTopic)
//        }
    }

    fun loadShowcasedTopics() {
        // Clear
        this.mainPage.clear()
        this.cacheManager.update()

        // Insert new entries
        val showcasedTopics = this.menuController.parser.getShowcasedEntryList()
        showcasedTopics.forEach { entryItem -> this.insertEntry(entryItem, true)}

        // Disable page buttons
        this.menuController.pageButtons.setEnabled(false)
    }

    fun navigate(topic: EksiTopic) {
        // Clear
        this.mainPage.clear()
        this.hyperlinks.clear()

        // Header
        this.mainPage.append(topic.getTitle(), "content-topic")

        // Entry contents
        val entryItemList =
            if(topic.page == 1) this.cacheManager.getEntriesByTitle(topic.getTitle()) // Using the first page from cache
            else topic.getEntryItems()

        // Can be done with this without caching (slower page load)
//        var entryItemList = topic.getEntryItems()
        entryItemList.forEach { this.insertEntry(it.entryItem, false) }

        // Update latest topic
        this.latestTopic = topic

        // Modify page buttons
        this.menuController.pageButtons.setEnabled(true)

        this.menuController.pageChoice.items.clear()
        for (i in 0 until this.getPageCountByTopic(topic)) this.menuController.pageChoice.items.add(i + 1)
        this.menuController.pageChoice.selectionModel.select(topic.page as Int?)
    }

    private fun getPageCountByTopic(topic: EksiTopic): Int = this.cacheManager.getEntriesByTitle(topic.getTitle()).let {
        return if(it.isEmpty()) 0 else it[0].pageCount
    }

    private fun insertEntry(entryItem: IEksiEntryItem, appendHeader: Boolean) {
        this.mainPage.let {
            // New line
            if (it.length != 0) it.append(StringUtils.getLineSeparator().repeat(3))

            // Content
            it.setParagraphStyle(it.currentParagraph, listOf("content"))
            if (appendHeader) it.append(entryItem.getTitle() + StringUtils.getLineSeparator(), "content-topic")
            it.append(entryItem.getContent() + StringUtils.getLineSeparator(), "content")

            // Date & author
            it.setParagraphStyle(it.currentParagraph, listOf("entry-info"))
            it.append(entryItem.getDate(), listOf("faded"))
            it.append(" ".repeat(2))
            it.append(entryItem.getAuthor().getName(), listOf("author", "link"))
            this.hyperlinks.add(Hyperlink(entryItem.getAuthor().getURL(), entryItem.getAuthor(), it.length))
        }
    }

    private fun StyleClassedTextArea.append(s: String) = this.append(s, "")
}