package org.codehaus.testdox.intellij.ui

trait ItemSelectionUI {

    def getSelectedItem: Any

    def setSelectedIndex(index: Int): Unit

    def show(): Unit

    def isOK: Boolean

    def wasCancelled: Boolean
}
