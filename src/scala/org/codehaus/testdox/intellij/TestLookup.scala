package org.codehaus.testdox.intellij

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.{PsiClass}

class TestLookup(val editorApi: EditorApi, sentenceTranslator: SentenceTranslator) {

  def isJavaFile(file: VirtualFile) = editorApi.isJavaFile(file)

  def getClass(testClassName: String): PsiClass = editorApi.getPsiClass(testClassName)

  def getClassName(file: VirtualFile): String = {
    val javaFile = editorApi.getPsiJavaFile(file)
    if (javaFile != null)
      javaFile.getPackageName() + "." + file.getNameWithoutExtension()
    else
      null
  }

  def getClass(file: VirtualFile): PsiClass = {
    val className = getClassName(file)
    if (className != null) editorApi.getPsiClass(className) else null
  }

  def getTestMethods(testClass: PsiClass): Array[TestMethod] = {
    if (testClass != null) {
      editorApi.getMethods(testClass)
          .filter {editorApi.isTestMethod(_)}
          .map {new TestMethod(_, editorApi, sentenceTranslator)}
    }
    else
      TestMethod.EMPTY_ARRAY
  }
}
