# ARICE (Auto Record Immutable Collections Extension)

An implementations of the link AutoRecord extension for customizing a record generation process with support for immutable collections.

## Documentation

For more information on how to use ARICE and all its features, please visit the project's [Wiki](https://github.com/pawellabaj/auto-record/wiki/ARICE).

## Getting started

### Maven

Add the following dependency to your `pom.xml` file:

```xml
<dependency>
    <groupId>pl.com.labaj.autorecord</groupId>
    <artifactId>auto-record</artifactId>
    <version>${auto-record.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>pl.com.labaj.autorecord</groupId>
    <artifactId>arice</artifactId>
    <version>${arice.version}</version>
    <scope>provided</scope>
</dependency>
```

### Gradle

Declare the following dependency in your `build.gradle` script:

```groovy
dependencies {
    annotationProcessor 'pl.com.labaj.autorecord:auto-record:${autoRecordVersion}',
    implementation 'pl.com.labaj.autorecord:arice:${ariceVersion}'
}
```

### IDE

Depending on your IDE you are likely to need to enable Annotation Processing in your IDE settings.
