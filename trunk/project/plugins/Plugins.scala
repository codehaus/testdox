import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val editsource = "org.clapper" % "sbt-editsource-plugin" % "0.3.1"
}
