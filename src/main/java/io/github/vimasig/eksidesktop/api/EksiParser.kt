package io.github.vimasig.eksidesktop.api

import io.github.vimasig.eksidesktop.utils.StringUtils.excludeLastWord
import io.github.vimasig.eksidesktop.utils.StringUtils.revertLines
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.URL
import kotlin.streams.toList

class EksiParser(var document: Document) {

    fun getEntryItemList(): List<IEksiEntryItem> {
        val list = mutableListOf<IEksiEntryItem>()
        val entryItemList = document.getElementsByAttributeValue("id", "entry-item-list")
        val entryItemContents = entryItemList.first().getElementsByClass("content")
        val entryItemInfos = entryItemList.first().getElementsByTag("footer")

        for (i in entryItemContents.indices) {
            val entryItemContent = entryItemContents[i]
            val entryItemInfo = entryItemInfos[i].getElementsByClass("info").first()
            list += this.buildEksiEntryItem(document.getElementsByTag("h1").first(), entryItemInfo, entryItemContent)
        }
        return list
    }

    fun getShowcasedEntryList(): List<IEksiEntryItem> {
        val showcasedTopics = arrayListOf<IEksiEntryItem>()
        val topicElements = document.getElementsByAttributeValue("id", "topic")
        val first = topicElements.first()
        val topicHeaders = first.getElementsByTag("h1")
        val topicContents = first.getElementsByClass("content")
        val topicInfos = first.getElementsByClass("info")
        for (i in topicHeaders.indices)
            showcasedTopics += this.buildEksiEntryItem(topicHeaders[i], topicInfos[i], topicContents[i])
        return showcasedTopics
    }

    fun getTopicList(): List<EksiTopic> {
        val topicList = document.getElementsByClass("topic-list").first()
        return topicList.getElementsByTag("li").stream()
            .filter { it.hasText() }
            .map { element ->
                object : EksiTopic() {
                    override fun getEntryItems(): List<EksiEntryPageContent> {
                        val doc = EksiBrowser.getDocument(this.getURL(), this.page)
                        val pageCount = doc
                                .getElementsByClass("pager")
                                .attr("data-pagecount")
                                .toInt()
                        return EksiParser(doc).getEntryItemList().stream().map { EksiEntryPageContent(pageCount, it) }.toList()
                    }
                    override fun getURL() = element.getElementsByTag("a").attr("href")
                    override fun getTitle() = element.getElementsByTag("a").getFirstText().excludeLastWord()
                    override fun getMessageCount() = element
                            .getElementsByTag("a").first()
                            .getElementsByTag("small").getFirstText()
                            .toInt()
                }
            }
            .toList()
    }

    private fun buildEksiEntryItem(title: Element, info: Element, content: Element): IEksiEntryItem {
        return object : IEksiEntryItem {
            override fun getTitle() = title.text()
            override fun getDate() = info.getFirstElementTextByClass("entry-date")
            override fun getAuthor() = object : IEksiAuthor {
                override fun getName() = info.getFirstElementTextByClass("entry-author")
                override fun getURL() = URL(EksiBrowser.EKSI_URL + info.getFirstElementByClass("entry-author").attr("href"))
            }
            override fun getContent() = content.text().revertLines()
        }
    }

    private fun Element.getFirstElementByClass(className: String) = this.getElementsByClass(className).first()
    private fun Element.getFirstElementTextByClass(className: String) = this.getFirstElementByClass(className).text()
    private fun Elements.getFirstText() = this.first().text()
}