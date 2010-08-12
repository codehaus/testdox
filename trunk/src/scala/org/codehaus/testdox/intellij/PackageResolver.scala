package org.codehaus.testdox.intellij

import PackageResolver._

class PackageResolver(sourcePackage: String) {

  def getPackage(packageTemplate: String): String = {
    if (packageTemplate == null || packageTemplate.trim.length == 0) {
      return ""
    }

    if (packageTemplate.equalsIgnoreCase(PACKAGE_TOKEN)) {
      return sourcePackage
    }

    if (packageTemplate.contains(PACKAGE_TOKEN))
      resolvePackage(packageTemplate)
    else
      trimNavigationMarkers(packageTemplate)
  }

  private def trimNavigationMarkers(packageTemplate: String) = packageTemplate.replaceAll("/?\\.\\.", "")

  private def resolvePackage(packageTemplate: String) =
    insertPackage(pop(sourcePackage, countNavigationMarkers(packageTemplate)), trimNavigationMarkers(packageTemplate))

  private def insertPackage(pkg: String, packageTemplate: String) = {
    val index = packageTemplate.indexOf(PACKAGE_TOKEN)
    if (index >= 0)
      clean(packageTemplate.substring(0, index)) + pkg + clean(packageTemplate.substring(index + PACKAGE_TOKEN.length()))
    else
      packageTemplate
  }

  private def clean(s: String) = s.replaceAll("/", ".")

  private def pop(sourcePackage: String, dotdotCount: Int): String = {
    var result = sourcePackage
    for (i <- 0 until dotdotCount) {
      val dot = result.lastIndexOf(".")
      if (dot >= 0) {
          result = result.substring(0, dot)
      } else {
          return ""
      }
    }
    result
  }

  private def countNavigationMarkers(packageTemplate: String): Int = {
    var count, current = 0
    var done = false
    while (!done) {
      var index = packageTemplate.indexOf(POP_TOKEN, current)
      if (index >= 0) {
        count += 1
        current = index + POP_TOKEN.length()
      } else {
        done = true
      }
    }
    return count
  }
}

object PackageResolver {
  val POP_TOKEN = "/.."
  val PACKAGE_TOKEN = "<package>"  
}
