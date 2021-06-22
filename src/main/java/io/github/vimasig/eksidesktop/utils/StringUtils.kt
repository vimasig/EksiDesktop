package io.github.vimasig.eksidesktop.utils

object StringUtils {

    fun getLineSeparator() = "\r\n"
    fun getLineSeparatorAsString() = "\\r\\n"
    fun String.removeString(string: String) = this.replace(string, "")
    fun String.excludeLastWord() = this.substring(0, this.lastIndexOf(" "))
    fun String.revertLines() = this.replace(getLineSeparatorAsString(), getLineSeparator())

}