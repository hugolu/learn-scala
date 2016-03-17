# The Simple Build Tool (SBT)

## Creating a Project Directory Structure for SBT
mkdir4sbt.sh:
```shell
#!/bin/sh

name=$1
version=$2
scalaVersion=2.11.7

mkdir -p src/{main,test}/{java,resources,scala}
mkdir lib project target

# create an initial build.sbt file

cat <<END > build.sbt
name := "$name"
version := "$version"
scalaVersion := "$scalaVersion"
END
```
- copy `mkdir4sbt.sh` to `/usr/local/bin` for convenience sake 

```shell
$ mkdir test
$ cd test
$ mkdir4sbt.sh test 1.0
$ tree
.
├── build.sbt
├── lib
├── project
├── src
│   ├── main
│   │   ├── java
│   │   ├── resources
│   │   └── scala
│   └── test
│       ├── java
│       ├── resources
│       └── scala
└── target

12 directories, 1 file
$ cat build.sbt
name := "test"
version := "1.0"
scalaVersion := "2.11.7"
```

## Compiling, Running, and Packaging a Scala Project with SBT
## Running Tests with SBT and ScalaTest
## Managing Dependencies with SBT
## Controlling Which Version of a Managed Dependency Is Used
## Creating a Project with Subprojects
## Using SBT with Eclipse
## Generating Project API Documentation
## Specifying a Main Class to Run
## Using GitHub Projects as Project Dependencies
## Telling SBT How to Find a Repository (Working with Resolvers)
## Resolving Problems by Getting an SBT Stack Trace
## Setting the SBT Log Level
## Deploying a Single, Executable JAR File
## Publishing Your Library
## Using Build.scala Instead of build.sbt
## Using a Maven Repository Library with SBT
## Building a Scala Project with Ant
