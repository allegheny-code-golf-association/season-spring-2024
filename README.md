![Green square on deep blue background with words "Allegheny Code Golf Association" in foreground, white](https://github.com/allegheny-college-cmpsc-201-spring-2024/golf/assets/1552764/d3ee6a91-74c9-482b-84eb-ec9a2e8dee05)

[![Language: Lark](https://img.shields.io/badge/Language-Lark-yellow.svg)](https://github.com/munificent/lark)
![Par: 108 bytes](https://img.shields.io/badge/Par-108_bytes-green)

# ⛳ Code Golf: Hole 10 - It's Hip to be a Square

Your task seems deceptively simple: print all squares from `1` to `100`. _But_ what happens when a language is _completely function-oriented_
and _lacks proper iterators_? Well, on a `Lark` (funny joke, Commissioner), we're going to find out.

When we write "print all squares", we mean that the output should look something like:
```bash
1
4
9
16
25
36
49
64
81
100
.
.
.
```

* [10/src/test/resources/main.lark](10/src/test/resources/main.aya)

### Note to players

This language is Aya-like; there's syntactic sugar that makes shortening our work possible. The official commissioner's suggestion is 
to solve it "long-hand" and then look at shortening it.

## Tips

Tips for golfing are available as general pointers:

* [General tips for golfing with any language](https://codegolf.stackexchange.com/questions/5285/tips-for-golfing-in-all-languages)

There exists a `README` and some examples of the langauge in the language repository:

* [Lark Language `README`](https://github.com/munificent/lark)
* [Lark Language examples](https://github.com/munificent/lark/tree/master/sample)

## Requirements

* this challenge must be completed using the `Lark` language
* the program must be written in the `10/src/test/resources/main.lark` file
* the program's output must span the numbers `1` to `100`

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
