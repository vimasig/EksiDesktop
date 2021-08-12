package io.github.vimasig.eksidesktop.ui.model

enum class EksiMessage(private val str: String) {
    TITLE("EksiDesktop"),
    CANNOT_OPEN_LINK("Link açılamadı: "),
    OPEN_LINK_CONFIRMATION("Aşağıdaki linki açmak istediğinize emin misiniz?");

    override fun toString(): String {
        return this.str
    }

    operator fun invoke(): String {
        return this.toString()
    }
}