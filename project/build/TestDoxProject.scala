import java.io.File
import sbt._

class TestDoxProject(info: ProjectInfo) extends DefaultProject(info) {
  override def dependencyPath = "lib"
  override def outputPath = "build"

  override def mainJavaSourcePath = "src" / "java"
  override def mainScalaSourcePath = "src" / "scala"
  
  override def testJavaSourcePath = "src" / "test"
  override def testScalaSourcePath = "test" / "scala"

  val ideaHome = system[File]("idea.home") 
  val ideaLib = Path.fromFile( ideaHome.value ) / "lib"

  override def unmanagedClasspath = super.unmanagedClasspath +++ descendents(ideaLib, "*.jar")
}
