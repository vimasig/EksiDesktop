package io.github.vimasig.eksidesktop.utils

import io.github.vimasig.eksidesktop.ui.model.EksiMessage
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.awt.Desktop
import java.net.URL

object DesktopUtils {

    fun browseURL(url: URL): Boolean {
        // Confirmation dialog
        Alert(Alert.AlertType.INFORMATION, EksiMessage.OPEN_LINK_CONFIRMATION() + StringUtils.getLineSeparator() + url, ButtonType.YES, ButtonType.CANCEL).let { alert ->
            alert.title = EksiMessage.TITLE()
            alert.headerText = ""
            alert.showAndWait().let {
                if (!it.isPresent || !it.get().buttonData.isDefaultButton)
                    return true
            }
        }

        // Browse URL
        when {
            this.isLinux() ->
                return try {
                    Runtime.getRuntime().exec("xdg-open $url")
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            else -> {
                if(Desktop.isDesktopSupported()) {
                    val desktop = Desktop.getDesktop()
                    if(desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            desktop.browse(url.toURI())
                            return true
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
               return false
            }
        }
    }

    private fun os() = System.getProperty("os.name").lowercase()
    private fun isLinux() = os().contains("nix") || os().contains("nux") || os().contains("aix")
}