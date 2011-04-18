import java.io.File
import sbt._

class TestDoxProject(info: ProjectInfo) extends DefaultProject(info) {

  // mapping Maven's project structure to TestDox's

  override def outputPath = "build"

  override def mainJavaSourcePath  = "src" / "java"
  override def mainScalaSourcePath = "src" / "scala"
  override def mainResourcesPath   = "src" / "resources"
  
  override def testJavaSourcePath  = "src" / "test"
  override def testScalaSourcePath = "test" / "scala"

  // loading all JAR's into the unmanaged classpath

  val ideaHome = system[File]("idea.home")
  val ideaLib = Path.fromFile(ideaHome.value) / "lib"

  override def unmanagedClasspath = super.unmanagedClasspath +++ descendents(ideaLib, "*.jar") +++ ("lib" ** "*.jar")

  override def compileOptions = ExplainTypes :: CompileOption("-target:jvm-1.5") :: super.compileOptions.toList

  // porting all key Ant tasks to SBT

  lazy val continuousIntegration = task { None } dependsOn(checkIdeaVersion, createTestReports, checkCoverage, zipPlugin)

  lazy val checkIdeaVersion = task { None }
  lazy val createTestReports = task { None } dependsOn test
  lazy val checkCoverage = task { None } dependsOn test

  override def defaultJarBaseName = projectName.value + "-" + projectVersion.value

  lazy val zipPlugin = task { zipPluginAction } dependsOn `package` describedAs "Zips up the plugin for deployment into IntelliJ IDEA"

  def stagingPath = outputPath / "staging" / projectName.value / "lib"

  def zipPluginAction: Option[String] = {
    FileUtilities.copy(List((outputPath ##) / defaultJarName), stagingPath, true, log)
    FileUtilities.copy((("lib" / "runtime" ##) ** "*.jar").get, stagingPath, true, log)
    FileUtilities.zip(List((outputPath / "staging" ##) / projectName.value), outputPath / (defaultJarBaseName + ".zip"), true, log)
    FileUtilities.clean(List(outputPath / "staging", outputPath / defaultJarName), log)
    None
  }
}
