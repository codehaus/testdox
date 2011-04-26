import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale.ENGLISH

import sbt._
import scala.io.Source
import org.clapper.sbtplugins.EditSourcePlugin

class TestDoxProject(info: ProjectInfo) extends DefaultProject(info) with EditSourcePlugin {

  // mapping Maven's project structure to TestDox's

  override def outputPath = "build"

  override def mainJavaSourcePath  = "src" / "java"
  override def mainScalaSourcePath = "src" / "scala"
  override def mainResourcesPath   = "src" / "resources"
  
  override def testJavaSourcePath  = "src" / "test"
  override def testScalaSourcePath = "test" / "scala"

  // loading all JAR's into the unmanaged classpath

  val ideaLib = Path.fromFile(system[String]("idea.home").value) / "lib"
  override def unmanagedClasspath = super.unmanagedClasspath +++ descendents(ideaLib, "*.jar") +++ ("lib" ** "*.jar")

  override def compileOptions = ExplainTypes :: CompileOption("-target:jvm-1.5") :: super.compileOptions.toList

  // porting Ant tasks to SBT

  lazy val continuousIntegration = task { None } dependsOn(checkIdeaVersion, createTestReports, checkCoverage, zipPlugin)

  lazy val checkIdeaVersion  = task { None }
  lazy val createTestReports = task { None } dependsOn test
  lazy val checkCoverage     = task { None } dependsOn test

  lazy val projectTitle             = property[String]
  lazy val projectDescription       = property[String]
  lazy val projectReleaseNotes      = property[String]
  lazy val projectUrl               = property[String]
  lazy val projectOrganizationEmail = property[String]
  lazy val projectOrganizationUrl   = property[String]

  val now = new Date
  val tokens = Map(
    "PROJECT.NAME"               -> projectName.value,
    "PROJECT.TITLE"              -> projectTitle.value,
    "PROJECT.VERSION"            -> (projectVersion.value + " (IDEA X)"),
    "PROJECT.DESCRIPTION"        -> projectDescription.value,
    "PROJECT.RELEASENOTES"       -> projectReleaseNotes.value,
    "PROJECT.URL"                -> projectUrl.value,
    "PROJECT.ORGANIZATION"       -> projectOrganization.value,
    "PROJECT.ORGANIZATION.EMAIL" -> projectOrganizationEmail.value,
    "PROJECT.ORGANIZATION.URL"   -> projectOrganizationUrl.value,

    "IDEA.SINCE-BUILD" -> "90.116",
    "IDEA.UNTIL-BUILD" -> "106.999",

    "DAY"   -> new SimpleDateFormat("d",    ENGLISH).format(now),
    "MONTH" -> new SimpleDateFormat("MM",   ENGLISH).format(now),
    "YEAR"  -> new SimpleDateFormat("yyyy", ENGLISH).format(now)
  )

  val metaInfDirectory   = outputPath / "resources" / "META-INF"
  val ideaPluginRegistry = metaInfDirectory / "idea-plugin-registry.xml"
  val pluginDescriptor   = metaInfDirectory / "plugin.xml"

  lazy val applyFilter = task {
    editSourceToFile(Source.fromFile(ideaPluginRegistry.absolutePath), tokens, ideaPluginRegistry.asFile)
    editSourceToFile(Source.fromFile(pluginDescriptor.absolutePath),   tokens, pluginDescriptor.asFile)
    None
  } dependsOn copyResources

  override def defaultJarBaseName = projectName.value + "-" + projectVersion.value

  override def packagePaths  = mainClasses +++ descendents((outputPath / "resources" ##), "*")
  override def packageAction = super.packageAction dependsOn applyFilter

  lazy val zipPlugin = task { zipPluginAction } dependsOn `package` describedAs "Zips up the plugin for deployment into IntelliJ IDEA"

  val stagingPath = outputPath / "staging" / projectName.value / "lib"
  val licenseFile = stagingPath / "LICENSE.txt"

  def zipPluginAction: Option[String] = {
    FileUtilities.copy(List((outputPath ##) / defaultJarName),  stagingPath, true, log)
    FileUtilities.copy((("lib" / "runtime" ##) ** "*.jar").get, stagingPath, true, log)
    FileUtilities.copy(List("LICENSE.TXT"),  stagingPath, true, log)
    editSourceToFile(Source.fromFile(licenseFile.absolutePath), tokens, licenseFile.asFile)
    FileUtilities.zip(List((outputPath / "staging" ##) / projectName.value), outputPath / (defaultJarBaseName + ".zip"), true, log)
    FileUtilities.clean(List(outputPath / "staging", outputPath / defaultJarName), log)
    None
  }
}
