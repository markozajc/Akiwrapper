[maven-img]: https://img.shields.io/maven-central/v/org.eu.zajc/akiwrapper.svg?label=Maven%20Central
[maven]: https://mvnrepository.com/artifact/org.eu.zajc/akiwrapper

[build-img]: https://github.com/markozajc/Akiwrapper/actions/workflows/test.yml/badge.svg?branch=master
[build]: https://github.com/markozajc/Akiwrapper/actions/workflows/test.yml?query=branch%3Amaster

> **Warning**\
> **Relocation notice for 1.6.1 and above:**\
> Akiwrapper's artifact has relocated from `com.github.markozajc:akiwrapper` to `org.eu.zajc:akiwrapper`. Additionally,
> the same change has been made on the base package name. You will need to change Akiwrapper's dependency's `groupId`
> in your pom.xml or build.gradle (as shown in the installation section) and you will need to replace
> `com.github.markozajc.akiwrapper` with `org.eu.zajc.akiwrapper` in your imports.

# Akiwrapper [![Maven central emblem][maven-img]][maven] [![Build status][build-img]][build]
Akiwrapper is a Java API wrapper for [Akinator](https://en.akinator.com/), the popular online
[20Q-type](https://en.wikipedia.org/wiki/Twenty_questions) game.

## Installation
#### Maven
Add the following dependency to your pom.xml:
```xml
<dependency>
    <groupId>org.eu.zajc</groupId>
    <artifactId>akiwrapper</artifactId>
    <version>1.6.1-1</version>
</dependency>
```
#### Gradle
Add the following dependency to your build.gradle:
```gradle
implementation group: 'org.eu.zajc', name: 'akiwrapper', version: '1.6.1-1'
```

## Usage

### Starting the game

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

### The game loop

You'll typically want to set up a question-answer loop. Fetch questions with
```java
Question question = aw.getQuestion();
```

Display the question to the player, collect their answer, and feed it to Akinator with
```java
aw.answer(Answer.YES);
``` 

If the player wishes to undo their previous answer, you can let do that with
```java
aw.undoAnswer();
```
You can undo answers all the way to the first question.

### Guesses

Akinator will occasionally try guessing what the player is thinking about.
```java
var guess = aw.suggestGuess()
if (guess != null) {
    // ask the player to confirm or reject the guess
    if (playerConfirmedGuess) {
        aw.confirmGuess(guess); // let Akinator know that the guess is right
        return; // finish the game
        
    } else {
        aw.rejectLastGuess();  // let Akinator know that the guess is not right - this also gives us a new question
    }
}
```
When a guess is available, the player should be asked to confirm it. If the guess is confirmed, we finish the game and
optionally let Akinator know. If the guess is rejected, we let Akinator know and continue. Akiwrapper also keeps track
of rejected guesses for you, so `suggestGuess()` never returns the same guess.
 
At some point (normally after question #80) Akinator will run out of questions to ask. This is indicated by
`aw.isExhausted()`. After there are no questions left, the last guess should be retrieved and shown to the player.

### Cleaning up

Unless you provide your own UnirestInstance to AkiwrapperBuilder, you should make sure to shut down the singleton 
instance that Akiwrapper uses by default after you're done with Akiwrapper (calling `System.exit()` also works):
```java
UnirestUtils.shutdownInstance();
```

---

That's it! If you need more help, be sure to check out the bundled
[example code](../master/example) to see how the library is used.

## Mirrors
* https://git.zajc.eu.org/akiwrapper.git/
* https://github.com/markozajc/Akiwrapper
