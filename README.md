# Akiwrapper
Akiwrapper is a fully-documented and easy-to-use Java API wrapper for Akinator

## Getting started
It's really easy to get started with Akiwrapper. First off, we'll need a new Akinator session
```java
Akiwrapper aw = new Akiwrapper();
```

Not, it's time to retrieve Akinator's first question
```java
Question q = aw.getCurrentQuestion();
```

_(you can get nicely formatted question with `Question#toString()`!)_

Now, let's answer it with YES
```java
q.answer(Level.YES);
```
This will also retrieve the next question

After answering A LOT of questions, you will occasionally get guesses from Akinator. You can check if there are any & retrieve them with
```java
if (aw.getCurrentQuestion().isEmpty()) {
    Guess[] guesses = aw.getGuesses();
}
```
_(of course, you can call Akiwrapper#getGuesses() every time an question is retrieved, but for the sake of performance, it's better to use Akiwrapper#getCurrentQuestion().isEmpty() instead)_

---
That's it! If you need more help, be sure to check src/com/mz/akiwrapper/examples for some ready-to-go examples.
