# AutoRecord

Java record newContext generator

[![Maven Central version](https://img.shields.io/maven-central/v/pl.com.labaj/auto-record)](https://mvnrepository.com/artifact/pl.com.labaj/auto-record)
[![javadoc](https://javadoc.io/badge2/pl.com.labaj/auto-record/javadoc.svg)](https://javadoc.io/doc/pl.com.labaj/auto-record)
[![Reproducible Builds](https://img.shields.io/badge/Reproducible_Builds-ok-success?labelColor=1e5b96)](https://github.com/jvm-repo-rebuild/reproducible-central#pl.com.labaj:auto-record)


[![CI Verify Status](https://github.com/pawellabaj/auto-record/actions/workflows/verify.yml/badge.svg?branch=main)](https://github.com/pawellabaj/auto-record/actions/workflows/verify.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pawellabaj%3Aauto-record&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=pawellabaj%3Aauto-record)
[![Sonatype Lift Status](https://lift.sonatype.com/api/badge/github.com/pawellabaj/auto-record)](https://lift.sonatype.com/results/github.com/pawellabaj/auto-record)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-2.1-4baaaa.svg)](.github/CODE_OF_CONDUCT.md)

## What is AutoRecord

AutoRecord is a code generator that helps you easily generate Java records. 
It provides an easy way to avoid writing repetitive boilerplate code. It generates the code with features such as:
* nullability checking
* builders - incorporating [Randgalt/record-builder](https://github.com/Randgalt/record-builder) library
* [memoization](https://en.wikipedia.org/wiki/Memoization)
* ignoring specified fields in `hashCode` and `equals` methods

AutoRecord allows users to customize generated records by:
* using annotation templates
* implementing custom extensions

## Why AutoRecord was created

Google [AutoValue](https://github.com/google/auto) has long been used as a way to work with _Value Classes_ in an easy way.
However, when Java [records](https://docs.oracle.com/en/java/javase/17/language/records.html) were introduced, they lacked some features that AutoValue had, such as nullability checking, builders, and memoization.
This is why AutoRecord was created.

## How to use AutoRecord

To use AutoRecord, simply annotate your interface with @AutoRecord:

```java
import pl.com.labaj.autorecord.AutoRecord;

@AutoRecord
interface Person {
    String name();
    int age();
}
```

AutoRecord will then generate a Java record class that implements your interface. The constructor parameters correspond, in order, to the interface methods:

```java
import static java.util.Objects.requireNonNull;

import java.lang.String;
import javax.annotation.processing.Generated;

@Generated("pl.com.labaj.autorecord.AutoRecord")
record PersonRecord(String name, int age) implements Person {
  PersonRecord {
    requireNonNull(name, "name must not be null");
  }
}
```

| :memo: Note                                                                                                                                                                                                               |
|:--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Here you can see example of generated record with all features provided by the library: [WithAllFeaturesRecord.java](https://gist.github.com/pawellabaj/c773e35a17e3f7f4d75d2829d75680df#file-withallfeaturesrecord-java) |

## Documentation

For more information on how to use AutoRecord and all its features, please visit the project's [Wiki](https://github.com/pawellabaj/auto-record/wiki).

## Getting started

### Maven

Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>pl.com.labaj</groupId>
    <artifactId>auto-record</artifactId>
    <version>${auto-record.version}</version>
</dependency>
```

### Gradle

Declare the following dependency in your `build.gradle` script:

```groovy
dependencies {
    annotationProcessor 'pl.com.labaj:auto-record:${autoRecordVersion}'
}
```

### IDE

Depending on your IDE you are likely to need to enable Annotation Processing in your IDE settings.

## Contributing
We welcome contributions from all developers! If you would like to contribute to AutoRecord, please refer to the [Contributing guide](.github/CONTRIBUTING.md) for more information on how to get started.

## License

This project is licensed under the [Apache 2.0 License](LICENSE).