package io.github.vimasig.eksidesktop.api

abstract class EksiTopic(var page: Int = 1) : IEksiTopic {

    fun backward() {
        if(this.page - 1 > 0)
            this.page--
    }
    fun forward(pageCount: Int) {
        if(this.page + 1 <= pageCount)
            this.page++
    }
}