# Contributing Guidelines
Thank you for considering contributing to our project! Here are some guidelines to follow when contributing:

## Code of Conduct
Please review and abide by our [Code of Conduct](CODE_OF_CONDUCT.md) before contributing.

## Issues and Bugs
If you find an issue or a bug in our project, please open an issue on our [issue tracker](https://github.com/pawellabaj/auto-record/issues). Please include a clear description of the issue, steps to reproduce it, and any relevant information about your environment.

## Feature Requests
If you have a feature request or an idea for our project, please open an issue on our [issue tracker](https://github.com/pawellabaj/auto-record/issues). Please include a clear description of the feature request or idea, the rationale behind it, and any other relevant information.

## Pull Requests
If you want to contribute code to our project, please open an issue our [issue tracker](https://github.com/pawellabaj/auto-record/issues) and assign it to yourself. The branch and draft pull request will be opened automatically.

* make sure that your code follows our [coding standards](#coding-standards)
* include a clear description of the changes in the pull request
* make sure that your code builds and passes all tests locally before submitting the pull request
* include tests for your changes if applicable
* make sure that your code does not introduce any new compiler warnings

## Coding Standards
* default [IntelliJ IDEA Code Style](https://www.jetbrains.com/help/idea/code-style.html#import-export-schemes) is used for code formatting
* [Maven](https://maven.apache.org/) is used as for our build system
* [JUnit 5](https://junit.org/junit5/) is used for unit testing


## Annotation Processing
Our project provides annotation processing to generate Java records based on annotated interfaces during compilation. Here are some guidelines to follow when working with annotation processing:

* make sure that your code follows the [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se17/html/index.html) and the [Java Annotation Processing API](https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/annotation/processing/package-summary.html).
* make sure that the processor is thread-safe and do not have any side-effects
* make sure that the processor generates valid Java code that can be compiled by the Java compiler

## License

By contributing to our project, you agree that your contributions will be licensed under the [Apache 2.0 License](LICENSE).
