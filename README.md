![Green square on deep blue background with words "Allegheny Code Golf Association" in foreground, white](https://github.com/allegheny-college-cmpsc-201-spring-2024/golf/assets/1552764/d3ee6a91-74c9-482b-84eb-ec9a2e8dee05)

[![Language: Jactl](https://img.shields.io/badge/Language-Jactl-blue.svg)](https://jactl.io/)
![Par: 1648 bytes](https://img.shields.io/badge/Par-1648_bytes-green)

# ⛳ Code Golf: Hole 11 - It's a bird! It's a plane! It's a...ptero-Jactl?



### Note to players

Once again, word to the homie, [@munificent](https://github.com/munificent) for the `Lark` language.

This language is Aya-like; there's syntactic sugar that makes shortening our work possible. The official commissioner's suggestion is 
to solve it "long-hand" and then look at shortening it.

## Tips

Tips for golfing are available as general pointers:

* [General tips for golfing with any language](https://codegolf.stackexchange.com/questions/5285/tips-for-golfing-in-all-languages)

There exists a `README` and a fairly deep page for the langauge:

* [Jactl Language `README`](https://github.com/jaccomoc/jactl)
* [Jactl Language site](https://jactl.io/)

## Requirements

* this challenge must be completed using the `Jactl` language
* the program must be written in the `10/src/test/resources/main.jactl` file
* the program's output must evaluate the `10` sentences in the main.input file

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
