package org.codehaus.testdox.intellij

import com.intellij.openapi.vfs.VirtualFileAdapter
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.psi._
import org.codehaus.testdox.intellij.config.Configuration

import collection.mutable.ListBuffer

class DeletionInterceptor(editorApi: EditorApi, config: Configuration, nameResolver: NameResolver) extends VirtualFileAdapter {

  private var deleting: Boolean =_

  private val taskCompletionMarker = new Runnable() {
    def run() { deleting = false }
  }

  override def beforeFileDeletion(event: VirtualFileEvent) {
    if (event.getRequestor.isInstanceOf[PsiManager]) {
      if (event.getFile.isDirectory) {
        if (config.deletePackageOccurrences) {
          deleteOtherPackageOccurrences(editorApi.getPsiDirectory(event.getFile))
        }
      } else if (config.autoApplyChangesToTests) {
        deleteCorrespondingTestClass(editorApi.getPsiJavaFile(event.getFile))
      }
    }
  }

  private def deleteOtherPackageOccurrences(deletedDirectory: PsiDirectory) {
    val deletedPackage = getPackage(deletedDirectory)
    if (!deleting && deletedDirectory != null && deletedPackage != null) {
      val deletableDirectories = retrieveOtherDeletablePackageOccurrences(deletedDirectory)
      if (deletableDirectories.length > 0) {
        val packageName = deletedPackage.getQualifiedName
        editorApi.deleteAsynchronously(deletableDirectories, buildQuestion(packageName, deletableDirectories), "Delete Other Package Occurrences", taskCompletionMarker)
        deleting = true
      }
    }
  }

  private def retrieveOtherDeletablePackageOccurrences(deletedDirectory: PsiDirectory): Array[PsiDirectory] = {
    val deletablePackageOccurrences = new ListBuffer[PsiDirectory]()
    getPackage(deletedDirectory).getDirectories foreach { directory =>
        if ((!directory.equals(deletedDirectory)) && (directory.isWritable)) {
          deletablePackageOccurrences += directory
        }
    }
    deletablePackageOccurrences.toArray[PsiDirectory]
  }

  private def getPackage(deletedDirectory: PsiDirectory) = JavaDirectoryService.getInstance().getPackage(deletedDirectory)

  private def buildQuestion(packageName: String, deletableDirectories: Array[PsiDirectory]) = {
    val question = new StringBuilder("Do you also want to delete the following occurrences of package '")
    question.append(packageName).append("'?\n ")
    deletableDirectories foreach { directory => question.append('\n').append(directory.getVirtualFile.getPath) }
    question.toString()
  }

  private def deleteCorrespondingTestClass(deletedClassFile: PsiJavaFile) {
    if (deletedClassFile != null) {
      val deletedClassName = deletedClassFile.getPackageName + '.' + deletedClassFile.getClasses()(0).getName
      if (nameResolver.isRealClass(deletedClassName)) {
        val testClassName = nameResolver.getTestClassName(deletedClassName)
        val testClass = editorApi.getPsiClass(testClassName)
        if (testClass != null) {
          editorApi.deleteAsynchronously(testClass)
        }
      }
    }
  }
}
