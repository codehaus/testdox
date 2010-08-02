package org.codehaus.testdox.intellij

import com.intellij.psi._
import org.codehaus.testdox.intellij.Mocks.MockableVirtualFile
import com.intellij.openapi.vfs.newvfs.impl.NullVirtualFile
import org.intellij.openapi.testing.{RealObjectBuilder, MockApplicationManager}
import com.intellij.history.LocalHistory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import org.codehaus.testdox.intellij.config.Configuration
import org.specs.Specification
import org.specs.mock.Mockito

object DeletionInterceptorSpec extends Specification with Mockito {

  private val PACKAGE_PATH = "src/java/com/acme"
  private val PACKAGE_NAME = "com.acme"
  private val CLASS_NAME = "Foo"

  private val FULLY_QUALIFIED_CLASS_NAME = PACKAGE_NAME + '.' + CLASS_NAME
  private val FULLY_QUALIFIED_TEST_CLASS_NAME = FULLY_QUALIFIED_CLASS_NAME + "Test"

  "DeletionInterceptor" should {

    val editorApi = mock[EditorApi]
    val nameResolver = mock[NameResolver]
    val config = new Configuration()

    val interceptor = new DeletionInterceptor(editorApi, config, nameResolver)

    doBeforeSpec {MockApplicationManager.reset()}

    doAfter {
//      there were noMoreCallsTo(editorApi)
      there were noMoreCallsTo(nameResolver)
    }

    // Package deletion

    "not intercept directory deletion" in {

      "if TestDox is not configured to delete package occurrences" in {
        config.setDeletePackageOccurrences(false)
        interceptor.beforeFileDeletion(createDirectoryDeletedEvent())
        there were noMoreCallsTo(editorApi)
      }

      "when triggered by a cancelled deletion" in {
        config.setDeletePackageOccurrences(false)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
        there were noMoreCallsTo(editorApi)
      }

      "when triggered by a local VCS operation" in {
        config.setDeletePackageOccurrences(false)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
        there were noMoreCallsTo(editorApi)
      }

      "when triggered by the CVS directory prunner" in {
        config.setDeletePackageOccurrences(false)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
        there were noMoreCallsTo(editorApi)
      }

      "if the directory being deleted does not represent a package" in {
        val deletedDirectory = mock[PsiDirectory]

        editorApi.getPsiDirectory(any[VirtualFile]) returns deletedDirectory

        val javaDirectoryService = mock[JavaDirectoryService]
        javaDirectoryService.getPackage(deletedDirectory) returns null

        MockApplicationManager.getMockApplication().registerComponent(classOf[JavaDirectoryService], javaDirectoryService)

        config.setDeletePackageOccurrences(true)
        interceptor.beforeFileDeletion(createDirectoryDeletedEvent())

        there was one(javaDirectoryService).getPackage(deletedDirectory)
      }

      "if the directory being deleted is the only occurrence of the package it represents" in {
        val deletedDirectory = mock[PsiDirectory]

        editorApi.getPsiDirectory(any[VirtualFile]) returns deletedDirectory

        val psiPackage = mock[PsiPackage]
        psiPackage.getDirectories() returns Array(deletedDirectory)

        val javaDirectoryService = mock[JavaDirectoryService]
        javaDirectoryService.getPackage(deletedDirectory) returns psiPackage

        MockApplicationManager.getMockApplication().registerComponent(classOf[JavaDirectoryService], javaDirectoryService)

        config.setDeletePackageOccurrences(true)
        interceptor.beforeFileDeletion(createDirectoryDeletedEvent())

        there were two(javaDirectoryService).getPackage(deletedDirectory)
        there was one(psiPackage).getDirectories()
      }

      "if other occurrences of the associated package are read-only" in {
        val deletedDirectory = mock[PsiDirectory]

        editorApi.getPsiDirectory(any[VirtualFile]) returns deletedDirectory

        val readOnlyDirectory = mock[PsiDirectory]
        readOnlyDirectory.isWritable returns false

        val psiPackage = mock[PsiPackage]
        psiPackage.getDirectories() returns Array(readOnlyDirectory)

        val javaDirectoryService = mock[JavaDirectoryService]
        javaDirectoryService.getPackage(deletedDirectory) returns psiPackage

        MockApplicationManager.getMockApplication().registerComponent(classOf[JavaDirectoryService], javaDirectoryService)

        config.setDeletePackageOccurrences(true)
        interceptor.beforeFileDeletion(createDirectoryDeletedEvent())

        there were two(javaDirectoryService).getPackage(deletedDirectory)
        there was one(psiPackage).getDirectories()
      }
    }

    "asynchronously delete other occurrences of the package represented by the directory being deleted" in {
      val anotherVirtualFile = mock[MockableVirtualFile]
      anotherVirtualFile.getPath() returns "src/test/com/acme"

      val psiPackage = mock[PsiPackage]
      val directory = mock[PsiDirectory]

      psiPackage.getQualifiedName() returns PACKAGE_NAME
      psiPackage.getDirectories() returns Array(directory)
      directory.isWritable() returns true
      directory.getVirtualFile() returns anotherVirtualFile

      val deletedDirectory = mock[PsiDirectory]
      editorApi.getPsiDirectory(any[VirtualFile]) returns deletedDirectory
      editorApi.deleteAsynchronously(any[Array[PsiDirectory]], any[String], any[String], any[Runnable])

      val javaDirectoryService = mock[JavaDirectoryService]
      javaDirectoryService.getPackage(deletedDirectory) returns psiPackage

      MockApplicationManager.getMockApplication().registerComponent(classOf[JavaDirectoryService], javaDirectoryService)

      config.setDeletePackageOccurrences(true)
      interceptor.beforeFileDeletion(createDirectoryDeletedEvent())

      there was atLeastOne(javaDirectoryService).getPackage(deletedDirectory)
    }

    // Class deletion

    "not intercept class deletion" in {

      "if TestDox is not configured to automatically apply changes to tests" in {
        config.setAutoApplyChangesToTests(false)
        interceptor.beforeFileDeletion(createFileDeletedEvent())
        there were noMoreCallsTo(editorApi)
      }

      "when triggered by a cancelled deletion" in {
        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(new VirtualFileEvent(new Object(), NullVirtualFile.INSTANCE, CLASS_NAME + ".java", null))
        there were noMoreCallsTo(editorApi)
      }

      "when triggered by a local VCS operation" in {
        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
        there were noMoreCallsTo(editorApi)
      }

      "when triggered by the CVS directory prunner" in {
        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
        there were noMoreCallsTo(editorApi)
      }

      "if the file being deleted is not a class in the project" in {
        editorApi.getPsiJavaFile(any[VirtualFile]) returns null

        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())

        there were noMoreCallsTo(editorApi)
      }

      "if the file being deleted is a test class" in {
        val javaFile = mock[PsiJavaFile]
        val javaClass = mock[PsiClass]

        editorApi.getPsiJavaFile(any[VirtualFile]) returns javaFile
        javaFile.getPackageName() returns PACKAGE_NAME
        javaFile.getClasses() returns Array(javaClass)
        javaClass.getName() returns "FooTest"
        nameResolver.isRealClass(FULLY_QUALIFIED_TEST_CLASS_NAME) returns false

        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createFileDeletedEvent())

        there were noMoreCallsTo(nameResolver)
      }

      "if the test class cannot be found" in {
        val javaFile = mock[PsiJavaFile]
        val javaClass = mock[PsiClass]

        editorApi.getPsiJavaFile(any[VirtualFile]) returns javaFile
        javaFile.getPackageName() returns PACKAGE_NAME
        javaFile.getClasses() returns Array(javaClass)
        javaClass.getName() returns CLASS_NAME
        nameResolver.isRealClass(FULLY_QUALIFIED_CLASS_NAME) returns true
        nameResolver.getTestClassName(FULLY_QUALIFIED_CLASS_NAME) returns FULLY_QUALIFIED_TEST_CLASS_NAME
        editorApi.getPsiClass(FULLY_QUALIFIED_TEST_CLASS_NAME) returns null

        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createFileDeletedEvent())
        
        there were noMoreCallsTo(nameResolver)
      }
    }

    "asynchronously delete a test class using the editor's API when the corresponding tested class is being deleted" in {
      val javaFile = mock[PsiJavaFile]
      val javaClass = mock[PsiClass]

      editorApi.getPsiJavaFile(any[VirtualFile]) returns javaFile
      javaFile.getPackageName() returns PACKAGE_NAME
      javaFile.getClasses() returns Array(javaClass)
      javaClass.getName() returns CLASS_NAME
      nameResolver.isRealClass(FULLY_QUALIFIED_CLASS_NAME) returns true
      nameResolver.getTestClassName(FULLY_QUALIFIED_CLASS_NAME) returns FULLY_QUALIFIED_TEST_CLASS_NAME
      editorApi.getPsiClass(FULLY_QUALIFIED_TEST_CLASS_NAME) returns javaClass
      editorApi.deleteAsynchronously(any[PsiClass])

      config.setAutoApplyChangesToTests(true)
      interceptor.beforeFileDeletion(createFileDeletedEvent())
      
      there were noMoreCallsTo(nameResolver)
    }
  }

  private def createVcsDirectoryPruningEvent() = new VirtualFileEvent(mock[LocalHistory], mock[VirtualFile], PACKAGE_PATH, null)

  private def createDirectoryDeletedEvent() = new RealObjectBuilder().virtualFileEvent()
      .withRequestor(mock[PsiManager])
      .withFileName(PACKAGE_PATH)
      .withIsDirectory(true)
      .build()

  private def createFileDeletedEvent() = new RealObjectBuilder().virtualFileEvent().withFileDeleted().build()
}
