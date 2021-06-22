package io.github.vimasig.eksidesktop.api

interface IEksiTopic {

    fun getEntryItems(): List<EksiEntryPageContent>
    fun getURL(): String
    fun getTitle(): String
    fun getMessageCount(): Int

}