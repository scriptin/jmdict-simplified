# Contributing to jmdict-simplified

## General advice

- Be polite, respect others
- Be patient, remember that this is a hobby project
- Do at least some basic research before asking for help

## Before creating a new issue/request

1. Make sure to read the [documentation](README.md).
   It covers most basic questions and gives guidance
   on how to set up and use the converter and JSON files
2. Make sure to check if similar issues are already opened
3. Make sure that your problem is indeed related to this project
   and does not originate from or caused by the original
   JMdict/JMnedict XML files or any third-party software

If you need help with setting up Gradle and/or Java/JVM/OpenJDK, please refer to the official docs:

- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html) - remember that
  this project comes with a bundled Gradle wrapper, so you don't have to install Gradle yourself
- Azul OpenJDK installation:
  [Mac OS](https://docs.azul.com/core/zulu-openjdk/install/macos),
  [Windows](https://docs.azul.com/core/zulu-openjdk/install/windows),
  [Debian-based Linux](https://docs.azul.com/core/zulu-openjdk/install/debian),
  [RPM-based Linux](https://docs.azul.com/core/zulu-openjdk/install/rpm-based-linux), etc.

## Creating a good bug report

1. Write a descriptive title. Think of a title you would like
   to see when searching for a solution to your problem.
    - Bad example: *"Missing data"* - Which data? Where?
    - Better: *"Missing attribute 'attr_name'"* - Where is this attribute supposed to be?
    - Good example: *"Missing attribute 'attr_name' on gloss elements in JMdict"*

2. Provide enough details to understand the problem:
    - Describe the result you expected and what you actually got
    - Include a sample of a dictionary, if necessary.
      For example, comparing the original XML file entry
      with its JSON counterpart to illustrate the difference.
    - Provide a command line output if you get an error.
      Preferably as a text, not a screenshot

3. If you can suggest a possible solution, please feel free to do so

## Creating a good feature request

1. Write a descriptive title (see above)

2. Describe your problem, use case, and/or motivation to ask for this feature.
   Sometimes, there might already exist a solution which you haven't thought of.
   Understanding what you're trying to achieve can help find a solution
   which would benefit others, e.g. by being more generic or approaching
   the problem from a different perspective.

3. If you have some specific technical requirements, please describe them.
