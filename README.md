![Green square on deep blue background with words "Allegheny Code Golf Association" in foreground, white](https://github.com/allegheny-college-cmpsc-201-spring-2024/golf/assets/1552764/d3ee6a91-74c9-482b-84eb-ec9a2e8dee05)

[![Language: codelike](https://img.shields.io/badge/Language-codelike-white.svg)](https://github.com/dospunk/codelike/tree/master)
![Par: 144 bytes](https://img.shields.io/badge/Par-144_bytes-green)

# ⛳ Code Golf: Hole 13 - Bit by Bit

How many bits does it take to store a given number? The world may never know except that, well, _you'll know_ because that's the challenge this week.
The hitch: we're using a 2D stack-based language to achieve it: something called `codelike`. There are a few modifications to pay attention to, as we've
taken it upon ourselves to write in a couple instructions to make this week possible:

|Command |Outcome              |
|:-------|:--------------------|
|`z`     |Copy the number `n` positions down in the stack where `n` is the current top value of the stack |
|`v`     |Print the number of values in the stack |

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
0
9
```
* this program requires _user input_: it _will not prompt you_ for input and will wait for input instead
  * this means that the `u` command is necessary for this program to function and test correctly
* to assist players there is a `debugging` mode; to enable this, go to the interpreter source and set the `debugging` variable to `true`
  * be sure to turn it off before you submit!

## Tips

Tips for golfing are available as general pointers:

* [`codelike` docs](https://github.com/dospunk/codelike/tree/master)
  * don't forget that we're working with a _modified version_ of the language with two additional commands described in the table above
* [General tips for golfing with any language](https://codegolf.stackexchange.com/questions/5285/tips-for-golfing-in-all-languages)

## Requirements

* this challenge must be completed using the `codelike` language
* the program must be written in the `13/src/test/resources/main.txt` file

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
