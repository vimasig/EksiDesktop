package io.github.vimasig.eksidesktop.api

import javafx.application.Platform
import javafx.scene.control.Label
import java.text.DecimalFormat
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.streams.toList

class EksiCacheManager(private val parser: EksiParser, private val cache: CopyOnWriteArrayList<EksiEntryPageContent> = CopyOnWriteArrayList(), private val status: Label) {

    private var runningThreads: Int = 0
    private var processed: Int = 0
    private var lastSize: Int = 0

    fun isRunning() = runningThreads != 0 && getProgress(Double) != 100.0

    fun update() {
        // Reset
        this.cache.clear()
        this.processed = 0

        val topicList = this.parser.getTopicList(); lastSize = topicList.size
        val availableProcessors: Int = Runtime.getRuntime().availableProcessors()

        for(i in 0 until availableProcessors) {
            val threadId: Int = i
            Thread {
                runningThreads++
                try {
                    var j = 0
                    var target: Int = threadId
                    while (target < topicList.size) {
                        cache.addAll(topicList[target].getEntryItems())
                        this.updateStatus()
                        j++
                        target = threadId + j * availableProcessors
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                runningThreads--
            }.let {
                it.name = "Cache thread #$threadId"
                it.isDaemon = true
                it.start()
            }
        }
    }

    fun getEntriesByTitle(title: String) = this.cache.stream()
            .filter { it.entryItem.getTitle() == title }
            .toList()

    private fun updateStatus() {
        this.processed++
        Platform.runLater { status.text = "${getProgress(String)}%" }
    }

    private fun Double.defaultStringFormat() = DecimalFormat("##.##").format(this)
    private inline fun <reified T> getProgress(retType: T): Any {
        val percentage = (processed.toDouble() / this.lastSize) * 100.0
        return when(retType) {
            is Double.Companion -> percentage
            is String.Companion -> percentage.defaultStringFormat()
            else -> throw IllegalArgumentException("Invalid getProgress type")
        }
    }
}