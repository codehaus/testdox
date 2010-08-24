package org.codehaus.testdox.intellij

import org.specs.SpecificationWithJUnit

object TestDoxNonProjectClassSpec extends SpecificationWithJUnit {

  "The test element of a Java class that is not in the current project" should {
    val testElement = TestDoxNonProjectClass.TEST_ELEMENT

    "have a corresponding textual representation" in {
      testElement.displayString must be equalTo "<font color=\"gray\">Not in current project</font>"
    }

    "use the warning icon as its graphical representation" in {
      testElement.icon must be equalTo Icons.getIcon(Icons.WARNING_ICON)
    }
  }
}
