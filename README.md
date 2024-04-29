![Green square on deep blue background with words "Allegheny Code Golf Association" in foreground, white](https://github.com/allegheny-college-cmpsc-201-spring-2024/golf/assets/1552764/d3ee6a91-74c9-482b-84eb-ec9a2e8dee05)

![Language: Lox](https://img.shields.io/badge/Language-Lox-pink.svg)
![Par: 181 bytes](https://img.shields.io/badge/Par-181_bytes-green)

```
How many bits does it take to store a given number? The world may never know except that, well, _you'll know_ because that's the challenge this week.
We're revisiting an old stand-by to complete this, though; how would we do it in `Lox`?

## Notes to players

* the output must contain _each_ product of division followed by the number of bits; that is, for the test number `256`:
```
128
64
32
16
8
4
2
1
9
668.5
334.25
167.125
83.5625
41.78125
20.890625
10.4453125
5.22265625
2.611328125
1.3056640625
0.65283203125
11
```
* this program  _does not take user input_; there are, however, only _two_ test cases which the program must run: 
  * `256`
  * `1337`
* these test cases must be included in the source, as `Lox` doesn't current take user input

To revisit the original challenge, head over to [Round 13](https://github.com/Allegheny-Code-Colf-Association/season-spring-2024/tree/round-13).

## Tips

* [General tips for golfing with any language](https://codegolf.stackexchange.com/questions/5285/tips-for-golfing-in-all-languages)

## Requirements

* this challenge must be completed using the `Lox` interpreter
  * the version of the interpreter contains everything we've done up to (and including) `Classes`
* the program must be written in the `17/src/test/resources/main.lox` file

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
