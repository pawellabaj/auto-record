# ARICE Utilities

Provides immutable versions of collections used by classes genrated by ARICE.

Please, see [WIKI](https://github.com/pawellabaj/auto-record/wiki/ARICE) for information.

## Usage

The classes in this package can also be used independently by your own classes.
In such case, import `arice-utils` directly to your project.

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