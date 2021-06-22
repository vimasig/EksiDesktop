package io.github.vimasig.eksidesktop.api

import io.github.vimasig.eksidesktop.utils.StringUtils
import io.github.vimasig.eksidesktop.utils.StringUtils.removeString
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException


object EksiBrowser {

    const val EKSI_URL = "https://eksisozluk.com"
    private val cookies: Map<String, String>

    init {
        try {
            cookies = Jsoup.connect(this.EKSI_URL).execute().cookies()
        } catch (e: Exception) {
            throw RuntimeException("Cannot initialize EksiBrowser", e)
        }
    }

    fun getDocument() = this.getDocument("/")
    fun getDocument(href: String) = this.getDocument(href, 0)
    fun getDocument(href: String, page: Int): Document {
        try {
            val url = this.EKSI_URL + href + if(page > 1) "&p=$page" else ""
            println("Requesting: $url")
            val res = Jsoup.connect(url)
                .cookies(this.cookies)
                .execute()
            val doc = Jsoup.parse(res.parse().html().removeString(StringUtils.getLineSeparatorAsString()))
            doc.select("br").append(StringUtils.getLineSeparatorAsString())
            doc.select("p").append(StringUtils.getLineSeparatorAsString().repeat(2))
            return doc
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}