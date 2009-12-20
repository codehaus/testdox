package org.codehaus.testdox.intellij.config

import org.codehaus.testdox.intellij.TemplateNameResolver

import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions
import scala.reflect.BeanProperty

@serializable
class Configuration {

  private var customPackages = List[String]()
  private var customPackagesAllowed: Boolean = false
  private var testNameTemplate = TemplateNameResolver.DEFAULT_TEMPLATE
  private var showFullyQualifiedClassName: Boolean = false

  @BeanProperty var alphabeticalSorting = false
  @BeanProperty var createTestIfMissing = true
  @BeanProperty var underscoreMode = false
  @BeanProperty var autoscrolling = false
  @BeanProperty var autoApplyChangesToTests = true
  @BeanProperty var deletePackageOccurrences = false
  @BeanProperty var testMethodPrefix = TemplateNameResolver.DEFAULT_PREFIX
  @BeanProperty var testMethodAnnotation = "@Test"
  @BeanProperty var usingAnnotations = false

  @transient
  private val support = new PropertyChangeSupport(this)

  def addPropertyChangeListener(listener: PropertyChangeListener) {
    support.addPropertyChangeListener(listener)
  }

  def removePropertyChangeListener(listener: PropertyChangeListener) {
    support.removePropertyChangeListener(listener)
  }

  @deprecated
  def getCustomPackagesAllowed = customPackagesAllowed

  @deprecated
  def setCustomPackagesAllowed(customPackagesAllowed: Boolean) {
    val old = this.customPackagesAllowed
    this.customPackagesAllowed = customPackagesAllowed
    support.firePropertyChange(Configuration.ALLOW_CUSTOM_PACKAGES, old, customPackagesAllowed)
  }

  @deprecated
  def getCustomPackages = JavaConversions.asList[String](new ListBuffer[String] ++ customPackages)

  @deprecated
  def setCustomPackages(newPackages: java.util.List[String]) {
    val oldPackages = customPackages;
    customPackages = (new ListBuffer[String] ++ JavaConversions.asBuffer(newPackages)).toList
    support.firePropertyChange(Configuration.CUSTOM_PACKAGES, oldPackages, customPackages)
  }

  @deprecated
  def getTestNameTemplate = testNameTemplate

  @deprecated
  def setTestNameTemplate(testNameTemplate: String) {
    val old = this.testNameTemplate
    this.testNameTemplate = testNameTemplate
    support.firePropertyChange(Configuration.TEST_NAME_TEMPLATE, old, testNameTemplate)
  }

  @deprecated
  def getShowFullyQualifiedClassName = showFullyQualifiedClassName

  @deprecated
  def setShowFullyQualifiedClassName(showFullyQualifiedClassName: Boolean) {
    val oldValue = this.showFullyQualifiedClassName
    this.showFullyQualifiedClassName = showFullyQualifiedClassName
    support.firePropertyChange(Configuration.SHOW_FULLY_QUALIFIED_CLASS_NAME, oldValue, showFullyQualifiedClassName)
  }

  def testMethodIndicator = if (usingAnnotations) testMethodAnnotation else testMethodPrefix
}

object Configuration {
  val ALLOW_CUSTOM_PACKAGES = "allowCustomPackages"
  val CUSTOM_PACKAGES = "customPackages"
  val TEST_NAME_TEMPLATE = "testNameTemplate"
  val SHOW_FULLY_QUALIFIED_CLASS_NAME = "showFullyQualifiedClassName"
}
