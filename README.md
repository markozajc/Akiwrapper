[central]: https://img.shields.io/maven-central/v/com.github.markozajc/akiwrapper.svg?label=Maven%20Central
[travis]: https://travis-ci.org/markozajc/Akiwrapper.svg?branch=master
[![travis]](https://travis-ci.org/markozajc/Akiwrapper.svg?branch=master)
![central]


# Akiwrapper
Akiwrapper is a fully-documented and easy-to-use Java API wrapper for Akinator.

## Installation
#### Maven
Put this: into your pom.xml (replace LATEST_VERSION with ![central]:
```xml
<dependency>
	<groupId>com.github.markozajc</groupId>
	<artifactId>akiwrapper</artifactId>
	<version>LATEST_VERSION</version>
</dependency>
```
You can find an example POM [here](https://github.com/markozajc/Akiwrapper/blob/master/example/pom.xml).

## Usage
It's really easy to get started with Akiwrapper. First off, we'll need to create a new Akinator API session:
```java
Akiwrapper aw = new AkiwrapperBuilder().build();
```

Now, it's time to retrieve Akinator's first question:
```java
Question q = aw.getCurrentQuestion();
```

Let's answer it with "YES":
```java
aw.answerCurrentQuestion(Answer.YES);
```
This will also return the next question

---

After repeating this process for some time (with different answers, of course), the probability of Akinator's guesses will rise to the point where it's almost certain (usually 85% is enough). You can check if there are any guesses with that probability & retrieve them with
```java
for (Guess guess : aw.getGuessesAboveProbability(0.85 /* you can specify your threshold between 0 and 1 */)) {
	// Do something with those guesses
}
```

---

There is a high chance Akinator will get at least one guess one but let's imagine the user has rejected all guesses. In this case, Akinator will run out of questions after a certain amount of answered questions. We need to watch out for this:
``` java
if (aw.answerCurrentQuestion(someAnswer) == null || aw.getCurrentQuestion() == null) {
	// Watch out! Akinator has ran out of questions! 
	// In this case,
	// - Akiwrapper#answerCurrentQuestion() will not throw an exception but rather return null no matter what
	// - Akiwrapper#getCurrentQuestion() will also keep returning null
}
```
If this happens, the best way to handle it is to let user see all guesses (no matter their probability) with iterating over `aw.getGuesses()` and asking user if this is their character on each. If they reject them all, make them feel good by telling them that they have finally defeated Akinator.


---
That's it! If you need more help, be sure to check an example [here](https://github.com/markozajc/Akiwrapper/tree/master/example) for an out-of-the-box working example.
