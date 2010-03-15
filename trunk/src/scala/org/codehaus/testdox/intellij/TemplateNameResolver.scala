package org.codehaus.testdox.intellij

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import org.codehaus.testdox.intellij.TemplateNameResolver._
import org.codehaus.testdox.intellij.config.Configuration

class TemplateNameResolver(configuration: Configuration) extends NameResolver with PropertyChangeListener {

  private var prefix = ""
  private var suffix = ""

  configureTemplate(configuration.getTestNameTemplate)
  configuration.addPropertyChangeListener(this)

  def propertyChange(event: PropertyChangeEvent) {
    if (Configuration.TEST_NAME_TEMPLATE == event.getPropertyName()) {
      configureTemplate(event.getNewValue().asInstanceOf[String])
    }
  }

  private def configureTemplate(template: String) {
    prefix = ""
    suffix = ""

    val tokens = template.split(NAME_TOKEN)
    if (tokens.length > 0) {
      if (tokens.length >= 2) {
        prefix = tokens(0)
        suffix = tokens(1)
      } else if (template.startsWith(NAME_TOKEN)) {
        suffix = tokens(0)
      } else if (template.endsWith(NAME_TOKEN)) {
        prefix = tokens(0)
      }
    }
  }

  def getRealClassName(className: String): String = {
    if (isRealClass(className)) return className

    var classNameOnly = extractClassNameWithoutPackageName(className)
    if (classNameOnly.startsWith(prefix)) {
      classNameOnly = classNameOnly.substring(prefix.length())
    }
    if (classNameOnly.endsWith(suffix)) {
      classNameOnly = classNameOnly.substring(0, classNameOnly.length() - suffix.length())
    }
    extractPackagePrefix(className) + classNameOnly
  }

  def getTestClassName(className: String): String = {
    if (isTestClass(className)) return className 

    val packagePrefix = extractPackagePrefix(className)
    val classNameOnly = extractClassNameWithoutPackageName(className)
    packagePrefix + prefix + classNameOnly + suffix
  }

  def getRealClassNameForDisplay(className: String) = {
    if (configuration.getShowFullyQualifiedClassName)
      getRealClassName(className)
    else
      extractClassNameWithoutPackageName(getRealClassName(className))
  }

  def isTestClass(className: String) = className != null && !isRealClass(className)

  def isRealClass(className: String): Boolean = {
    if (className == null) return false

    val classNameOnly = extractClassNameWithoutPackageName(className)
    !(classNameOnly.startsWith(prefix) && classNameOnly.endsWith(suffix))
  }

  private def isClassUnderPackage(className: String) = className.contains('.')

  private def extractClassNameWithoutPackageName(className: String) = {
    if (isClassUnderPackage(className))
      className.substring(className.lastIndexOf('.') + 1)
    else
      className
  }

  private def extractPackagePrefix(className: String) = {
    if (isClassUnderPackage(className))
      className.substring(0, className.lastIndexOf('.') + 1)
    else
      ""
  }
}

object TemplateNameResolver {
  val NAME_TOKEN = "<classname>"
  val DEFAULT_TEMPLATE = NAME_TOKEN + "Test"
  val DEFAULT_PREFIX = "test"
}
