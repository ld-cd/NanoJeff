name := "NanoJeff"
version := "1.0"
organization := "com.github.ld-cd"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "com.github.spinalhdl" % "spinalhdl-core_2.11" % "1.3.8",
  "com.github.spinalhdl" % "spinalhdl-lib_2.11" % "1.3.8"
)

fork := true
