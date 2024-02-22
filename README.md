![Green square on deep blue background with words "Allegheny Code Golf Association" in foreground, white](https://github.com/allegheny-college-cmpsc-201-spring-2024/golf/assets/1552764/d3ee6a91-74c9-482b-84eb-ec9a2e8dee05)

![Language: Java](https://img.shields.io/badge/Language-Java-brown.svg)
![Par: 772 bytes](https://img.shields.io/badge/Par-1057_bytes-green)

# ⛳ Code Golf: Hole 5 - ISBN or Nah

Books are great. I mean, really great. Don't take it from me: ISBN true (always; that's a good joke). For a very long time, books were serialized using a `10`-digit number referred to as an `ISBN-10` which constituted part of the barcode for a given book. Each is unique. However, we need to know if someone's trying to scam the system.

Your task is to determine if an `ISBN-10` is valid or not. Here're the rules:

* `ISBN-10` numbers are `10` digits
  * `9` of them constitute a number
  * `1` (the last) is a "check digit"

From a source more authoritative than me:

> To verify an ISBN, calculate 10 times the first digit, plus 9 times the second digit, plus 8 times the third digit and so on until we add 1 times the last digi>

Note: the final check digit will either be one of the set `0`-`9` or an `X`.

The code representing this is already written. But, can you make it shorter?

## Tips

Tips for golfing are available for Prolog language and as general pointers:

* [Tips for Golfing in Java](https://codegolf.stackexchange.com/questions/6671/tips-for-golfing-in-java)
* [General tips for golfing with any language](https://codegolf.stackexchange.com/questions/5285/tips-for-golfing-in-all-languages)

## Requirements

* this challenge must be completed using the `Java` language
* the program must run and correctly check the `5` `ISBN-10` numbers in `5/test/resources/test.isbns`
* the program must be written in the `5/src/main/java/com/golf/five/Main.java` file

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
