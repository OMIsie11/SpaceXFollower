# Contributing

Do You want to help open source? Awesome! ❤️

## Before submitting a Pull Request please

### 1. Run Unit Tests
Currently only unit tests are ran on GitHub actions (due to the fact that instrumented tests could fail when no data is present in app - correction needed).
Two example ways to do this:
1. just run (test) directory by clicking on it
2. run in terminal: **./gradlew testDebugUnitTest** (command example for MacOS)

### 2. Use [ktlint](https://github.com/pinterest/ktlint) to format code and match style
(command examples for MacOS)
1. check formatting: **./gradlew ktlintCheck**
2. try to auto format files: **./gradlew ktlintCheck**

## Thanks!😀
