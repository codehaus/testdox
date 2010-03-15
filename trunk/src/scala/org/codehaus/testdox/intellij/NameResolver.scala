package org.codehaus.testdox.intellij

trait NameResolver {

  def getRealClassNameForDisplay(className: String): String

  def isTestClass(className: String): Boolean

  def isRealClass(className: String): Boolean

  def getRealClassName(className: String): String

  def getTestClassName(className: String): String
}
