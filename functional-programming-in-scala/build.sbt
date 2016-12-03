name := "fpinscala"
version := "0.0.1"
scalaVersion := "2.11.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

initialCommands in console := """import org.scalatest.FunSuite"""
