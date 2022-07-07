# Akiwrapper [![Maven central emblem](https://img.shields.io/maven-central/v/com.github.markozajc/akiwrapper.svg?label=Maven%20Central)](https://mvnrepository.com/artifact/com.github.markozajc/akiwrapper)
Akiwrapper is a Java API wrapper for [Akinator](https://en.akinator.com/), the popular online
[20Q-type](https://en.wikipedia.org/wiki/Twenty_questions) game.

## Installation
#### Maven
Add the following dependency to your pom.xml:
```xml
<dependency>
	<groupId>com.github.markozajc</groupId>
	<artifactId>akiwrapper</artifactId>
	<version>1.5.2</version>
</dependency>
```
#### Gradle
Add the following dependency to your build.gradle:
```gradle
implementation group: 'com.github.markozajc', name: 'akiwrapper', version: '1.5.2'
```

## Usage
To access the Akinator API, you'll need an Akiwrapper object. One can be created like so:
```java
Akiwrapper aw = new AkiwrapperBuilder().build();
```

If you, for example, wish to use a different language that the default English, or if you wish Akinator to guess
something other than characters, you may use the following setup:
```java
Akiwrapper aw = new AkiwrapperBuilder()
	.setLanguage(Language.GERMAN)
	.setGuessType(GuessType.PLACE)
	.build();
```
(keep in mind that not all language-guesstype combinations are supported, though all languages support `CHARACTER`)

You'll likely want to set up a question-answer loop afterwards. Fetch questions with
```java
Question question = aw.getQuestion();
```

Display the question to the user, collect their answer, and feed it to Akinator with
```java
aw.answer(Answer.YES);
``` 

If the player wishes to undo their previous answer, you can let Akinator know with
```java
aw.undoAnswer();
```

Akinator will propose a list of guesses after each answer, coupled with their determined probabilities. You can get all
guesses above a certain probability with
```java
aw.getGuessesAboveProbability(0.85f); // 85% seems to be the sweet spot, though you're free to use anything you want
```
Let the player review each guess, but keep track of the declined ones, as Akinator will send you the same guesses over
and over if he feels like it.
 
At some point Akinator will run out of questions to ask. This is indicated by `aw.getCurrentQuestion()` equalling null.
If and when this happens, fetch and propose all remaining guesses (this time without a probability filter) with
```java
aw.getGuesses()
```
and propose each one to the player. This also marks the absolute end of the game. 

Unless you provide your own UnirestInstance to AkiwrapperBuilder, you should make sure to shut down the singleton 
instance that Akiwrapper uses by default after you're done with Akiwrapper:
```java
UnirestUtils.shutdownInstance();
```

That's it! If you need more help, be sure to check the bundled example
[here](https://github.com/markozajc/Akiwrapper/tree/master/example) for an out-of-the-box working implementation.

## Available on:
* https://git.zajc.eu.org/akiwrapper.git/
* https://github.com/markozajc/gogarchiver/