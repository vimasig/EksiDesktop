package io.github.vimasig.eksidesktop.api

interface IEksiEntryItem {

    fun getTitle(): String
    fun getDate(): String
    fun getAuthor(): IEksiAuthor
    fun getContent(): String

}