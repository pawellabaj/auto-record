# Auto Record Utilities

Provides classes used for memoization of methods in generated record.

Please, see [WIKI](https://github.com/pawellabaj/auto-record/wiki) for information.

## Usage

The classes in this package can also be used independently to add memoization capabilities to your own classes. 
In such case, use `auto-record-utils` directly.

### Maven

Add the following dependency to your `pom.xml` file:

```xml

<dependency>
    <groupId>pl.com.labaj.autorecord</groupId>
    <artifactId>auto-record-utils</artifactId>
    <version>${auto-record.version}</version>
</dependency>
```

### Gradle

Declare the following dependency in your `build.gradle` script:

```groovy
dependencies {
    annotationProcessor 'pl.com.labaj.autorecord:auto-record-utils:${autoRecordVersion}'
}