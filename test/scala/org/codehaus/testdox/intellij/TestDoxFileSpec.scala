package org.codehaus.testdox.intellij

import org.specs.SpecificationWithJUnit
import org.codehaus.testdox.intellij.ui.TestDoxTableModel

object TestDoxFileSpec extends SpecificationWithJUnit {

  val testDoxFile = new TestDoxFile(null, null, null, null, TestMethod.EMPTY_ARRAY) {
      def updateModel(model: TestDoxTableModel) { }
  }

  "By default, a TestDox file" can {
    "not represent a test class" in {
      testDoxFile.isTestedClass() must be equalTo false
    }

    "not be unit tested by default" in {
      testDoxFile.canBeUnitTested() must be equalTo false
    }

    "not navigate to the test class" in {
      testDoxFile.canNavigateToTestClass() must be equalTo false
    }

    "not navigate to the tested class" in {
      testDoxFile.canNavigateToTestedClass() must be equalTo false
    }

    "be represented as text" in {
      testDoxFile.toString() must be equalTo
          (testDoxFile.getClass().getName() + " { file: null; className: null; testClass: null; testedClass: null }")
    }
  }
}
