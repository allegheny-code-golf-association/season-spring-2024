![Green square on deep blue background with words "Allegheny Code Golf Association" in foreground, white](https://github.com/allegheny-college-cmpsc-201-spring-2024/golf/assets/1552764/d3ee6a91-74c9-482b-84eb-ec9a2e8dee05)

![Language: JASIC](https://img.shields.io/badge/Language-JASIC-black.svg)
![Par: 282 bytes](https://img.shields.io/badge/Par-282_bytes-green)

# ⛳ Code Golf: Hole 6 - Back to BASIC(s) (er...JASIC(s)?)

This one's a blast from the past: a re-implementation of the original BASIC programming language using a Java-based interpreter
written by none other than the potentate of programming languages, [Bob Nystrom](https://github.com/munificent/)! And, this one is a classic
introduction to programming test. Your goal is to print the following `10`x`10` grid of stars:
```
**********
**********
**********
**********
**********
**********
**********
**********
**********
**********
```
However, you're using _a dialect_ of a larger language. Interestingly enough, _there are no `for` loops_ in this version of BASIC.
This may tempt you to use only `print` statements to display your creation, but ther is a way to cut over `180` bytes from the par
solution and another `100` from the `print`-statement-only version.

Take advantage of the [examples](https://github.com/munificent/jasic/tree/master/sample) that the interpreter's author provides and
the `README` to the interpreter repository (linked below). These will arm you with _all_ of the resources you need. There are so
few instructions in the language that the introductory comment in the interpreter shows you _every one of them_.

> Note: this week is a bit different in that _the par program is not provided for you_. However, the par score `282` represents
> the most abstruse way the commissioner could think of doing this in the language. It's your job to not only figure out _how this
> program should be written_: you've got to winnow bytes from your creation until you've got it to the smallest state possible.

## Tips

Tips for golfing are available for the JASIC language and as general pointers:

* [JASIC Interpreter](https://github.com/munificent/jasic/blob/master/com/stuffwithstuff/Jasic.java)
* [General tips for golfing with any language](https://codegolf.stackexchange.com/questions/5285/tips-for-golfing-in-all-languages)

## Requirements

* this challenge must be completed using the `JASIC` dialect of the `BASIC` language (resource linked above)
* the program must run and correctly print the `10`x`10` grid of `*` characters
* the program must be written in the `6/src/test/resources/test.jas` file

## Infrastructure

If you're familiar with running Java programs, you can feel free to `javac` or use your own framework to your heart's content. 
However, this repository leverages the build lifecycle using Apache Maven. Several challenges will require Java, it might be advantageous 
for you to do so.

Given the prevalence of VSCode installs, the league provides the following resources for you should you want to install and use
Maven in VSCode.

### Installing Java resources 

This toolchain uses:

* Java JDK/JRE
* Apache Maven
* Microsoft VSCode

Along with downloading and installing each component piecemeal, VSCode's Marketplace features plugins that accommodate both the Java runtime and Maven integration. After installing VSCode, if not already installed:

* Download the appropriate Java resources from the [VSCode "Java in Visual Studio Code" guide](https://code.visualstudio.com/docs/languages/java), namely:
  * The [Microsoft Build of OpenJDK](https://www.microsoft.com/openjdk)
* The [VSCode Extension Pack for Java](https://code.visualstudio.com/docs/java/java-build) which features the Maven build platform
* Install Apache Maven according to relevant OS instructions below

#### Note for Linux distribution users

Your lives are a bit easier. To download and install the necessary Java development kit and runtime, use the following commands (if using a Debian-derived distribution such as Ubuntu):

* `apt-get install default-jdk`
* `apt-get install default-jre`

### Installing Apache Maven

#### Windows

* Download the [latest `zip` archive of Apache Maven](https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip)
* Create a folder in your `C:` drive partition at `C:\maven`
* Extract the contents of the `zip` file to the `C:\maven` directory

#### Mac OSX

* Use `brew` to install the latest version of Apache Maven: `brew install maven`

#### Debian-based Linux distributions

* Use `apt-get` to install the latest version of Apache Maven: `apt-get install maven`

### Running with Maven

The following lifecycle steps are key to running this project with Maven:

|Lifecycle step |Purpose |
|:--------------|:-------|
|`clean`        |Removes all compiled targets |
|`compile`      |Rebuilds compiled targets|
|`test`         |Runs the test that the evaluator runs|
