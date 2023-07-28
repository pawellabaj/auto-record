# Auto Record API

Annotations to mark interfaces to be processed.

Please, see [WIKI](https://github.com/pawellabaj/auto-record/wiki) for information.

## Usage 

If you use annotation processing in your project, the library will be provided as a dependency of [Auto Record](https://github.com/pawellabaj/auto-record/tree/main/modules/auto-record).

If you just want to mark your sources (ieg. annotations are being processed in other project or build step), 
use `auto-record-api` directly.

### Maven

Add the following dependency to your `pom.xml` file:

```xml

<dependency>
    <groupId>pl.com.labaj</groupId>
    <artifactId>auto-record-api</artifactId>
    <version>${auto-record.version}</version>
</dependency>
```

### Gradle

Declare the following dependency in your `build.gradle` script:

```groovy
dependencies {
    annotationProcessor 'pl.com.labaj:auto-record-api:${autoRecordVersion}'
}