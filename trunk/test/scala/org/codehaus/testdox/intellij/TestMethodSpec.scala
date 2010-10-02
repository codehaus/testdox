package org.codehaus.testdox.intellij

import com.intellij.psi.PsiMethod
import org.codehaus.testdox.intellij.actions.RenameTestAction
import org.codehaus.testdox.intellij.config.Configuration
import org.specs.SpecificationWithJUnit
import org.specs.mock.JMocker

object TestMethodSpec extends SpecificationWithJUnit with JMocker {

  "TestMethod" should {
    val editorApi = mock[EditorApi]
    val psiMethod = mock[PsiMethod]

    "use the editor API to jump to the corresponding method" in {
      expect { one(editorApi).jumpToPsiElement(psiMethod) will returnValue(true) }

      val testMethod = new TestMethod(psiMethod, editorApi, null)
      testMethod.jumpToPsiElement() must be equalTo true
    }

    "return zero when compared to an object that is not a test method" in {
      val testMethod = new TestMethod(null, null, null)
      val testInterface = new TestInterface(null, null, null, null)

      testMethod.compareTo(testInterface) must be equalTo 0
    }

    "use its display string to define its natural order for comparison" in {
      val methodName1 = "someMethod"
      val methodName2 = "someOtherMethod"
      val psiMethod2 = mock[PsiMethod]

      expect {
        atLeast(1).of(psiMethod).getName()  will returnValue(methodName1)
        atLeast(1).of(psiMethod2).getName() will returnValue(methodName2)
      }

      val sentenceTranslator = new SentenceTranslator(new Configuration)
      val testMethod1 = new TestMethod(psiMethod,  editorApi, sentenceTranslator)
      val testMethod2 = new TestMethod(psiMethod2, editorApi, sentenceTranslator)

      testMethod2.compareTo(testMethod1) must be > 0
    }

    "use its display string as its textual representation" in {
      val sentenceTranslator = new SentenceTranslator(new Configuration())
      val testMethod = new TestMethod(psiMethod, editorApi, sentenceTranslator)

      expect { atLeast(1).of(psiMethod).getName() will returnValue("someMethod") }

      testMethod.toString() must be eq(testMethod.displayString)
    }

    "always enable the representation of an action when asked to update it" in {
      val presentation = new RenameTestAction().getTemplatePresentation()
      presentation.setEnabled(false)

      new TestMethod(null, null, null).update(presentation)

      presentation.isEnabled() must be equalTo true
    }
  }
}
