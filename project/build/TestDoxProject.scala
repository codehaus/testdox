import java.io.File
import sbt._

class TestDoxProject(info: ProjectInfo) extends DefaultProject(info) {

  // mapping Maven's project structure to TestDox's

  override def outputPath = "build"

  override def mainJavaSourcePath = "src" / "java"
  override def mainScalaSourcePath = "src" / "scala"
  override def mainResourcesPath = "src" / "resources"
  
  override def testJavaSourcePath = "src" / "test"
  override def testScalaSourcePath = "test" / "scala"

  // loading all JAR's into the unmanaged classpath

  val ideaHome = system[File]("idea.home")
  val ideaLib = Path.fromFile(ideaHome.value) / "lib"

  override def unmanagedClasspath = super.unmanagedClasspath +++ descendents(ideaLib, "*.jar") +++ ("lib" ** "*.jar")

  override def compileOptions = ExplainTypes :: CompileOption("-target:jvm-1.5") :: super.compileOptions.toList

  // porting all key Ant tasks to SBT

  lazy val build = task { None } describedAs "Runs the build used for continuous integration" dependsOn(clean, createTestReports, checkCoverage, createArtifact)

  lazy val createTestReports = task { None }
  lazy val checkCoverage = task { None }
  lazy val createArtifact = task { createArtifactAction; None } describedAs "Zips up the plugin for deployment into IntelliJ IDEA" dependsOn compile

  def createArtifactAction {
    log.info("zipping up plugin for distribution...")
  }
}
