# ARICE API

Annotations to mark methods in an interface, used during annotation processing with ARICE usage.

Please, see [WIKI](https://github.com/pawellabaj/auto-record/wiki/ARICE) for information.

## Usage

If you use annotation processing in your project, the library will be provided as a dependency of [ARICE](https://github.com/pawellabaj/auto-record/tree/main/arice/extension).

If you just want to mark your sources (ieg. annotations are being processed in other project or build step),
use `arice-api` directly.

### Maven

Add the following dependency to your `pom.xml` file:

```xml

<dependency>
    <groupId>pl.com.labaj.autorecord</groupId>
    <artifactId>arice-api</artifactId>
    <version>${arice.version}</version>
</dependency>
```

### Gradle

Declare the following dependency in your `build.gradle` script:

```groovy
dependencies {
    annotationProcessor 'pl.com.labaj.autorecord:arice-api:${ariceVersion}'
}