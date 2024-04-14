[maven-img]: https://img.shields.io/maven-central/v/org.eu.zajc/akiwrapper.svg?label=Maven%20Central
[maven]: https://mvnrepository.com/artifact/org.eu.zajc/akiwrapper

[build-img]: https://github.com/markozajc/Akiwrapper/actions/workflows/test.yml/badge.svg?branch=master
[build]: https://github.com/markozajc/Akiwrapper/actions/workflows/test.yml?query=branch%3Amaster

> [!WARNING]
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
    <version>2.0.0</version>
</dependency>
```
#### Gradle
Add the following dependency to your build.gradle:
```gradle
implementation group: 'org.eu.zajc', name: 'akiwrapper', version: '2.0.0'
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
    .setTheme(Theme.OBJECT)
    .build();
```
(keep in mind that not all language-theme combinations are supported, though all languages support `CHARACTER`)

### The game loop

Akinator sends two types of queries: questions (*"Is your character an X?"*, *"Does your character Y?"*) and guesses
(*"Is this your character?"*). You'll typically want to set up a query-answer loop. Fetch the first query with
```java
Query query = aw.getCurrentQuery();
```
Then split your logic based on the type using `instanceof`:
```java
if (query instanceof Question) {
    // Show the question, get an answer
} else if (query instanceof Guess) {
    // Show the guess, get a rejection or confirmation
} else if (query == null) {
    // Akinator has run out of questions, the player wins
}
```

#### Questions
Questions can be responded to in two ways: by answering them
```java
query = ((Question) query).answer(Answer.YES); // or any other Answer
```
... or by undoing them
```java
query = ((Question) query).undoAnswer();
```
Doing either will return the next query or `null` if there are none left (after answering question #80). You can also
ignore the return value and use Akiwrapper#getCurrentQuery() instead.

#### Guesses
Guesses can also be responded to in two ways: by rejecting them
```java
query = ((Guess) query).reject();
```
... or by confirming them
```java
((Guess) query).confirm()
```
Confirming a guess ends the game, meaning no more queries are returned past that point. This is also why
`Guess#confirm()` lacks a return value.

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
