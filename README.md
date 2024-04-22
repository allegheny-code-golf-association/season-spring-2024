![Green square on deep blue background with words "Allegheny Code Golf Association" in foreground, white](https://github.com/allegheny-college-cmpsc-201-spring-2024/golf/assets/1552764/d3ee6a91-74c9-482b-84eb-ec9a2e8dee05)

[![Language: Taxi](https://img.shields.io/badge/Language-Taxi-yellow.svg)](https://bigzaphod.github.io/Taxi/)
![Par: 3202 bytes](https://img.shields.io/badge/Par-3202_bytes-green)

# ⛳ Code Golf: Hole 14 - Going the Distance

In computational linguistics, there exists a simple, but descriptive index of the difference between any two words or phrases: a thing called
"Levenshtein distance." Described much more simply than the Wikipedia page for the concept, it's really a number that describes _how many changes
would need to be made_ to any two words in order to create the equivalent string. For example:
```
line -> lane: distance 1
```
In the above example, there's really only `1` letter different between them. So, we'd say that they have a Levenshtein distance of `1`. 
```
driven -> coding: distance 6
```
For this example, everything needs to change! So, we'd have a distance of `6`.

That's great. What's even greater? Implementing the functionality in a language that's about...driving a distance. 

This challenge uses a language called `Taxi`, which doesn't leave much room for proper golfing. In short, the language is all about driving a (you
guessed it), _taxi_. 

## Notes to players

The creator of the language, @bigzaphod, provides a helpful map for our endeavor:

![A big map of Townsville](https://bigzaphod.github.io/Taxi/map-big.png)

The following table<sup>†</sup> describes the various locations available in Townsville, the town where you'll drive a taxi:

<table border="0" cellpadding="0" cellspacing="1">
<tr><td bgcolor="#dadada">
  <table border="0" cellpadding="3" cellspacing="1">
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Addition Alley</b></td>
    <td bgcolor="white">adds numerical passengers together, anything non-numeric is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Auctioneer School</b></td>
    <td bgcolor="white">converts string passengers to uppercase, non-string is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Bird's Bench</b></td>
    <td bgcolor="white">one passenger can wait here until later, but only 1
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Charboil Grill</b></td>
    <td bgcolor="white">convert a numerical passenger to a single ASCII character (string) or vice-versa, strings longer than 1 are an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Chop Suey</b></td>
    <td bgcolor="white">takes a string passenger and breaks it up into individual string passengers that hold one character each, so &quot;Hi&quot; results in 2 passengers: &quot;H&quot; and &quot;i&quot;, non-string is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Collator Express</b></td>
    <td bgcolor="white">tests if the first string passenger is less than the second and returns the first if true or no one if not true, non-string is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Crime Lab</b></td>
    <td bgcolor="white">tests if all dropped off string passengers are equal to each other, if so returns 1 passenger with the value, otherwise no passenger is returned, non-string is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Cyclone</b></td>
    <td bgcolor="white">makes clones of passengers, drop 1 off, get original plus 1 copy back, drop 3 off, get original 3, plus 3 copies back, etc.
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Divide and Conquer</b></td>
    <td bgcolor="white">divides numerical passengers, anything non-numeric is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Equal's Corner</b></td>
    <td bgcolor="white">tests if all dropped off numerical passengers are equal to each other, if so returns 1 passenger with the value, otherwise no passenger is returned, non-numeric is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Firemouth Grill</b></td>
    <td bgcolor="white">any number of passengers can be dropped off here, but picked up in random and unknown order
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Fueler Up</b></td>
    <td bgcolor="white">gas station: 1.92/gallon
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Go More</b></td>
    <td bgcolor="white">gas station: 1.75/gallon
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Heisenberg's</b></td>
    <td bgcolor="white">pickup an unspecified random integer
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Joyless Park</b></td>
    <td bgcolor="white">passengers dropped off here form a FIFO queue so they can be picked up again later
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Knots Landing</b></td>
    <td bgcolor="white">inverts boolean logic via numerical passengers: non-zero becomes 0, 0 becomes 1, non-numerical is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>KonKat's</b></td>
    <td bgcolor="white">concatenates string passengers, anything non-string is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Little League Field</b></td>
    <td bgcolor="white">converts string passengers to lowercase, non-string is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Magic Eight</b></td>
    <td bgcolor="white">tests if the first passenger is less than the second and returns the first if true or no one if not true, non-numerical is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Multiplication Station</b></td>
    <td bgcolor="white">multiplies numerical passengers, anything non-numeric is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Narrow Path Park</b></td>
    <td bgcolor="white">passengers dropped off here form a stack (LIFO) so they can be picked up again later
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Post Office</b></td>
    <td bgcolor="white">drop off string passengers to print to stdout, pickup a passenger to read a string line from stdin
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Riverview Bridge</b></td>
    <td bgcolor="white">passengers dropped off at Riverview Bridge seem to always fall over the side and into the river thus the driver collects no pay, but at least the pesky passenger is gone
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Rob's Rest</b></td>
    <td bgcolor="white">one passenger can wait here until later, but only 1
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Rounders Pub</b></td>
    <td bgcolor="white">rounds numerical passengers, non-numerical is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Starchild Numerology</b></td>
    <td bgcolor="white">pickup a specified numerical value
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Sunny Skies Park</b></td>
    <td bgcolor="white">passengers dropped off here form a FIFO queue so they can be picked up again later
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Taxi Garage</b></td>
    <td bgcolor="white">starting and termination point
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>The Babelfishery</b></td>
    <td bgcolor="white">translates a numerical passenger to a string passenger or vice-versa
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>The Underground</b></td>
    <td bgcolor="white">takes 1 numerical passenger and subtracts 1, if the result is 0 or less than 0, no passenger is returned otherwise the result is returned, non-numerical is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Tom's Trims</b></td>
    <td bgcolor="white">removes whitespace from beginning and ending of string passengers, non-string is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Trunkers</b></td>
    <td bgcolor="white">truncates numerical passengers to an integer, non-numerical is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>What's The Difference</b></td>
    <td bgcolor="white">subtracts numerical passengers, anything non-numeric is an error
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Writer's Depot</b></td>
    <td bgcolor="white">pickup a specified string
</td>
  </tr>
  <tr valign="top">
    <td bgcolor="white" width="205" align="center" nowrap><b>Zoom Zoom</b></td>
    <td bgcolor="white">gas station: 1.45/gallon
</td>
  </tr>
  </table>
</td></tr>
</table>

<sup>†</sup>: this table is taken directly from the [classic Taxi website](https://bigzaphod.github.io/Taxi/)

## Tips

Tips for golfing are available as general pointers:

* [General tips for golfing with any language](https://codegolf.stackexchange.com/questions/5285/tips-for-golfing-in-all-languages)

## Requirements

* this challenge must be completed using the `Taxi` language
* the program must be written in the `14/src/test/resources/main.txt` file

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
