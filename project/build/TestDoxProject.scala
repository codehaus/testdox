import java.io.File
import sbt._
import webbytest.HtmlTestsProject

class TestDoxProject(info: ProjectInfo) extends DefaultProject(info) with HtmlTestsProject {

  override def outputPath = "build"

  override def mainJavaSourcePath = "src" / "java"
  override def mainScalaSourcePath = "src" / "scala"
  override def mainResourcesPath = "src" / "resources"
  
  override def testJavaSourcePath = "src" / "test"
  override def testScalaSourcePath = "test" / "scala"

  val ideaHome = system[File]("idea.home")
  val ideaLib = Path.fromFile(ideaHome.value) / "lib"

  override def unmanagedClasspath = super.unmanagedClasspath +++ descendents(ideaLib, "*.jar") +++ ("lib" ** "*.jar")
}
