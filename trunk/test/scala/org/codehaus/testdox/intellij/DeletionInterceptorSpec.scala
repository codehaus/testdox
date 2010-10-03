package org.codehaus.testdox.intellij

import com.intellij.psi._
import org.codehaus.testdox.intellij.Mocks.MockableVirtualFile
import com.intellij.openapi.vfs.newvfs.impl.NullVirtualFile
import com.intellij.history.LocalHistory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileEvent
import org.codehaus.testdox.intellij.config.Configuration
import org.specs.SpecificationWithJUnit
import org.specs.mock.{ClassMocker, JMocker}
import org.jmock.lib.concurrent.Synchroniser
import org.intellij.openapi.testing.{VirtualFileEventBuilder, MockApplicationManager}

object DeletionInterceptorSpec extends SpecificationWithJUnit with JMocker with ClassMocker {

  val PACKAGE_PATH = "src/java/com/acme"
  val PACKAGE_NAME = "com.acme"
  val CLASS_NAME = "Foo"

  val FULLY_QUALIFIED_CLASS_NAME = PACKAGE_NAME + '.' + CLASS_NAME
  val FULLY_QUALIFIED_TEST_CLASS_NAME = FULLY_QUALIFIED_CLASS_NAME + "Test"

  "DeletionInterceptor" should {
    val editorApi = mock[EditorApi]
    val nameResolver = mock[NameResolver]
    val config = new Configuration()

    val interceptor = new DeletionInterceptor(editorApi, config, nameResolver)

    doBeforeSpec { MockApplicationManager.reset() }

    doBefore { context.setThreadingPolicy(new Synchroniser()) }

    "not intercept directory deletion" in {

      "if TestDox is not configured to delete package occurrences" in {
        expect { never(editorApi).getPsiDirectory(any[VirtualFile]) }

        config.setDeletePackageOccurrences(false)
        interceptor.beforeFileDeletion(createDirectoryDeletedEvent())
      }

      "when triggered by a cancelled deletion" in {
        expect { never(editorApi).getPsiDirectory(any[VirtualFile]) }

        config.setDeletePackageOccurrences(false)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
      }

      "when triggered by a local VCS operation" in {
        expect { never(editorApi).getPsiDirectory(any[VirtualFile]) }

        config.setDeletePackageOccurrences(false)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
      }

      "when triggered by the CVS directory prunner" in {
        expect { never(editorApi).getPsiDirectory(any[VirtualFile]) }

        config.setDeletePackageOccurrences(false)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
      }

      "if the directory being deleted does not represent a package" in {
        val deletedDirectory = mock[PsiDirectory]
        val javaDirectoryService = mock[JavaDirectoryService]

        expect {
          one(editorApi).getPsiDirectory(any[VirtualFile]) will returnValue(deletedDirectory)
          one(javaDirectoryService).getPackage(deletedDirectory) will returnValue(null)
        }

        MockApplicationManager.getMockApplication().registerComponent(classOf[JavaDirectoryService], javaDirectoryService)

        config.setDeletePackageOccurrences(true)
        interceptor.beforeFileDeletion(createDirectoryDeletedEvent())
      }

      "if the directory being deleted is the only occurrence of the package it represents" in {
        val deletedDirectory = mock[PsiDirectory]
        val psiPackage = mock[PsiPackage]
        val javaDirectoryService = mock[JavaDirectoryService]

        expect {
          one(editorApi).getPsiDirectory(any[VirtualFile]) will returnValue(deletedDirectory)
          one(psiPackage).getDirectories() will returnValue(Array(deletedDirectory))
          exactly(2).of(javaDirectoryService).getPackage(deletedDirectory) will returnValue(psiPackage)
        }

        MockApplicationManager.getMockApplication().registerComponent(classOf[JavaDirectoryService], javaDirectoryService)

        config.setDeletePackageOccurrences(true)
        interceptor.beforeFileDeletion(createDirectoryDeletedEvent())
      }

      "if other occurrences of the associated package are read-only" in {
        val deletedDirectory = mock[PsiDirectory]
        val readOnlyDirectory = mock[PsiDirectory]
        val psiPackage = mock[PsiPackage]
        val javaDirectoryService = mock[JavaDirectoryService]

        expect {
          one(editorApi).getPsiDirectory(any[VirtualFile]) will returnValue(deletedDirectory)
          one(readOnlyDirectory).isWritable will returnValue(false)
          one(psiPackage).getDirectories() will returnValue(Array(readOnlyDirectory))
          exactly(2).of(javaDirectoryService).getPackage(deletedDirectory) will returnValue(psiPackage)
        }

        MockApplicationManager.getMockApplication().registerComponent(classOf[JavaDirectoryService], javaDirectoryService)

        config.setDeletePackageOccurrences(true)
        interceptor.beforeFileDeletion(createDirectoryDeletedEvent())
      }
    }

    "asynchronously delete other occurrences of the package represented by the directory being deleted" in {
      val anotherVirtualFile = mock[MockableVirtualFile]
      val psiPackage = mock[PsiPackage]
      val directory = mock[PsiDirectory]
      val deletedDirectory = mock[PsiDirectory]
      val javaDirectoryService = mock[JavaDirectoryService]

      expect {
        one(anotherVirtualFile).getPath() will returnValue("src/test/com/acme")
        one(psiPackage).getQualifiedName() will returnValue(PACKAGE_NAME)
        one(psiPackage).getDirectories() will returnValue(Array(directory))
        one(directory).isWritable() will returnValue(true)
        one(directory).getVirtualFile() will returnValue(anotherVirtualFile)

        one(editorApi).getPsiDirectory(any[VirtualFile]) will returnValue(deletedDirectory)
        one(editorApi).deleteAsynchronously(any[Array[PsiDirectory]], any[String], any[String], any[Runnable])
        atLeast(1).of(javaDirectoryService).getPackage(deletedDirectory) will returnValue(psiPackage)
      }

      MockApplicationManager.getMockApplication().registerComponent(classOf[JavaDirectoryService], javaDirectoryService)

      config.setDeletePackageOccurrences(true)
      interceptor.beforeFileDeletion(createDirectoryDeletedEvent())
    }

    "not intercept class deletion" in {

      "if TestDox is not configured to automatically apply changes to tests" in {
        expect { never(editorApi).getPsiJavaFile(any[VirtualFile]) }

        config.setAutoApplyChangesToTests(false)
        interceptor.beforeFileDeletion(createFileDeletedEvent())
      }

      "when triggered by a cancelled deletion" in {
        expect { never(editorApi).getPsiJavaFile(any[VirtualFile]) }

        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(new VirtualFileEvent(new Object(), NullVirtualFile.INSTANCE, CLASS_NAME + ".java", null))
      }

      "when triggered by a local VCS operation" in {
        expect { never(editorApi).getPsiJavaFile(any[VirtualFile]) }

        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
      }

      "when triggered by the CVS directory prunner" in {
        expect { never(editorApi).getPsiJavaFile(any[VirtualFile]) }

        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createVcsDirectoryPruningEvent())
      }

      "if the file being deleted is not a class in the project" in {
        expect { one(editorApi).getPsiJavaFile(any[VirtualFile]) will returnValue(null) }

        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createFileDeletedEvent())
      }

      "if the file being deleted is a test class" in {
        val javaFile = mock[PsiJavaFile]
        val javaClass = mock[PsiClass]

        expect {
          one(editorApi).getPsiJavaFile(any[VirtualFile]) will returnValue(javaFile)
          one(javaFile).getPackageName() will returnValue(PACKAGE_NAME)
          one(javaFile).getClasses() will returnValue(Array(javaClass))
          one(javaClass).getName() will returnValue("FooTest")
          one(nameResolver).isRealClass(FULLY_QUALIFIED_TEST_CLASS_NAME) will returnValue(false)
        }

        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createFileDeletedEvent())
      }

      "if the test class cannot be found" in {
        val javaFile = mock[PsiJavaFile]
        val javaClass = mock[PsiClass]

        expect {
          one(editorApi).getPsiJavaFile(any[VirtualFile]) will returnValue(javaFile)
          one(javaFile).getPackageName() will returnValue(PACKAGE_NAME)
          one(javaFile).getClasses() will returnValue(Array(javaClass))
          one(javaClass).getName() will returnValue(CLASS_NAME)
          one(nameResolver).isRealClass(FULLY_QUALIFIED_CLASS_NAME) will returnValue(true)
          one(nameResolver).getTestClassName(FULLY_QUALIFIED_CLASS_NAME) will returnValue(FULLY_QUALIFIED_TEST_CLASS_NAME)
          one(editorApi).getPsiClass(FULLY_QUALIFIED_TEST_CLASS_NAME) will returnValue(null)
        }

        config.setAutoApplyChangesToTests(true)
        interceptor.beforeFileDeletion(createFileDeletedEvent())
      }
    }

    "asynchronously delete a test class using the editor's API when the corresponding tested class is being deleted" in {
      val javaFile = mock[PsiJavaFile]
      val javaClass = mock[PsiClass]

      expect {
        one(editorApi).getPsiJavaFile(any[VirtualFile]) will returnValue(javaFile)
        one(javaFile).getPackageName() will returnValue(PACKAGE_NAME)
        one(javaFile).getClasses() will returnValue(Array(javaClass))
        one(javaClass).getName() will returnValue(CLASS_NAME)
        one(nameResolver).isRealClass(FULLY_QUALIFIED_CLASS_NAME) will returnValue(true)
        one(nameResolver).getTestClassName(FULLY_QUALIFIED_CLASS_NAME) will returnValue(FULLY_QUALIFIED_TEST_CLASS_NAME)
        one(editorApi).getPsiClass(FULLY_QUALIFIED_TEST_CLASS_NAME) will returnValue(javaClass)
        one(editorApi).deleteAsynchronously(any[PsiClass])
      }

      config.setAutoApplyChangesToTests(true)
      interceptor.beforeFileDeletion(createFileDeletedEvent())
    }
  }

  private def createVcsDirectoryPruningEvent() = new VirtualFileEvent(mock[LocalHistory], mock[VirtualFile], PACKAGE_PATH, null)

  private def createDirectoryDeletedEvent() = new VirtualFileEventBuilder()
      .withRequester(mock[PsiManager])
      .withFileName(PACKAGE_PATH)
      .withIsDirectory(true)
      .build()

  private def createFileDeletedEvent() = new VirtualFileEventBuilder()
      .withRequester(mock[PsiManager])
      .withFileName(CLASS_NAME + ".java")
      .build()
}
