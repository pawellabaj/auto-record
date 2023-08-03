# ARICE Utilities

Provides classes used copy collections to their appropriate immutable versions during generating record.

Please, see [WIKI](https://github.com/pawellabaj/auto-record/wiki/ARICE) for information.

## Usage

If you use annotation processing in your project, the library will be provided as a dependency of [ARICE](https://github.com/pawellabaj/auto-record/tree/main/arice/extension).

The classes in this package can also be used independently to add memoization capabilities to your own classes.
In such case, import `arice-utils` directly.

### Maven

Add the following dependency to your `pom.xml` file:

```xml

<dependency>
    <groupId>pl.com.labaj.autorecord</groupId>
    <artifactId>arice-utils</artifactId>
    <version>${arice.version}</version>
</dependency>
```

### Gradle

Declare the following dependency in your `build.gradle` script:

```groovy
dependencies {
    annotationProcessor 'pl.com.labaj.autorecord:arice-utils:${ariceVersion}'
}