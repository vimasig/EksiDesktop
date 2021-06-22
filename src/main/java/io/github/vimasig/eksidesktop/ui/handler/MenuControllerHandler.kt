package io.github.vimasig.eksidesktop.ui.handler

import io.github.vimasig.eksidesktop.ui.MenuController

abstract class MenuControllerHandler(protected val menuController: MenuController) {
    abstract fun init()
}