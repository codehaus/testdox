package org.codehaus.testdox.intellij.ui

import org.codehaus.testdox.intellij._
import org.codehaus.testdox.intellij.config.Configuration

import javax.swing.table.DefaultTableModel

import scala.collection.mutable.ListBuffer
import scala.util.Sorting

class TestDoxTableModel(configuration: Configuration) extends DefaultTableModel {

  private val definitionOrderData = new ListBuffer[TestElement]()
  private val alphaOrderData = new ListBuffer[TestElement]()

  var hasDox = false

  override def isCellEditable(row: Int, column: Int) = false

  override def getValueAt(row: Int, column: Int): Object = getDox(row)

  private def getDox(index: Int): Object = {
    if (index < 0 || index >= definitionOrderData.size) {
      return TestDoxNonJavaFile.TEST_ELEMENT
    }
    if (configuration.alphabeticalSorting) alphaOrderData(index) else definitionOrderData(index)
  }

  override def getColumnCount = 1

  override def getColumnClass(columnIndex: Int): Class[_] = classOf[TestElement]

  override def getRowCount = if (definitionOrderData == null) 0 else definitionOrderData.size

  def setNotJava() {
    clearLists()

    definitionOrderData += TestDoxNonJavaFile.TEST_ELEMENT
    alphaOrderData += TestDoxNonJavaFile.TEST_ELEMENT
    hasDox = false

    fireDataChange()
  }

  def setTestDoxForNonProjectClass(file: TestDoxFile) {
    clearLists()

    hasDox = false
    definitionOrderData += TestDoxNonProjectClass.TEST_ELEMENT
    alphaOrderData += TestDoxNonProjectClass.TEST_ELEMENT

    prependTestClassAndNotify(file)
  }

  def setTestDoxForInterface(file: TestDoxFile) {
    clearLists()

    hasDox = false
    definitionOrderData += TestDoxInterface.TEST_ELEMENT
    alphaOrderData += TestDoxInterface.TEST_ELEMENT

    prependTestClassAndNotify(file)
  }

  def setTestDoxForClass(file: TestDoxFile) {
    clearLists()

    val testMethods = file.testMethods
    if (testMethods.length == 0) {
      hasDox = false
      definitionOrderData += TestDoxClass.NO_DOX_ELEMENT
      alphaOrderData += TestDoxClass.NO_DOX_ELEMENT
    } else {
      hasDox = true
      definitionOrderData ++= testMethods
      alphaOrderData ++= testMethods

      var sortedElements = alphaOrderData.toArray[TestElement]
      Sorting.quickSort[TestElement](sortedElements)
      alphaOrderData.clear()
      alphaOrderData.insertAll(0, sortedElements)
    }

    prependTestClassAndNotify(file)
  }

  private def prependTestClassAndNotify(file: TestDoxFile) {
    definitionOrderData.insert(0, file.testClass)
    alphaOrderData.insert(0, file.testClass)
    fireDataChange()
  }

  private def fireDataChange() {
    fireTableDataChanged()
  }

  private def clearLists() {
    definitionOrderData.clear()
    alphaOrderData.clear()
  }

  def sortInAlphabeticalOrder() {
    if (hasDox) {
      configuration.alphabeticalSorting = true
      fireDataChange()
    }
  }

  def sortInDefinitionOrder() {
    configuration.alphabeticalSorting = false
    fireDataChange()
  }
}
