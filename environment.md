# Environment

## Install Java/Scala/SBT on Mac

### Install Java JDK
- [Java SE Downloads](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

```shell
$ wget http://download.oracle.com/otn-pub/java/jdk/8u111-b14/jdk-8u111-macosx-x64.dmg
$ java -version
```

### Install scala
```shell
$ brew install scala
$ scala -version
```

### Install sbt
```shell
$ brew install sbt
$ sbt sbt-version
```

## Source code

### clone the git repository 
```shell
$ mkdir -p $HOME/github
$ cd $HOME/github
$ git clone https://github.com/hugolu/learn-scala
```

### sync the source
```shell
$ cd $HOME/github/learn-scala
$ git pull
```
```shell
$ git commit -m "update"
$ git push
```
