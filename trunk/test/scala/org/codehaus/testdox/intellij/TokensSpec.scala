package org.codehaus.testdox.intellij

import org.codehaus.testdox.intellij.PackageResolver.PACKAGE_TOKEN
import org.codehaus.testdox.intellij.TemplateNameResolver.NAME_TOKEN

import org.specs.Specification

object TokensSpec extends Specification {

  val templateSuffix = "Foo"

  "TestDox" can {
    "use the default package token as a regular expression" in {
      val template = PACKAGE_TOKEN + templateSuffix
      template.split(PACKAGE_TOKEN).toSeq must be equalTo Seq("", templateSuffix)
    }

    "use the default name token as a regular expression" in {
      val template = NAME_TOKEN + templateSuffix
      template.split(NAME_TOKEN).toSeq must be equalTo Seq("",  templateSuffix)
    }
  }
}
