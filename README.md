# seatsio-java, the official Seats.io Java client library

[![](https://jitpack.io/v/seatsio/seatsio-java.svg)](https://jitpack.io/#seatsio/seatsio-java)

## Installing seatsio-java

seatsio-java is available as a maven-style package. To use it, you must first add jitpack as a repository in build.gradle or pom.xml:

```
// build.gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}

// pom.xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Then you can refer to seatsio-java as a regular package:

```
// build.gradle
dependencies {
  compile 'com.github.seatsio:seatsio-java:27'
}

// pom.xml
<dependency>
  <groupId>com.github.seatsio</groupId>
  <artifactId>seatsio-java</artifactId>
  <version>27</version>
</dependency>
```

## Java version

You need at least Java 8 to use seatsio-java.

## Versioning

seatsio-java only uses major version numbers: 5, 6, 7 etc. Each release - backwards compatible or not - receives a new major version number.

The reason: we want to play safe and assume that each release _might_ break backwards compatibility.

## Examples

### Creating a chart and an event

```java
SeatsioClient client = new SeatsioClient(<SECRET KEY>); // can be found on https://app.seats.io/settings
Chart chart = client.charts.create();
Event event = client.events.create(chart.key);
System.out.println("Created event with key " + event.key);
```

### Booking objects

```java
SeatsioClient client = new SeatsioClient(<SECRET KEY>);
client.events.book(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"));
```

### Releasing objects

```java
SeatsioClient client = new SeatsioClient(<SECRET KEY>);
client.events.release(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"));
```

### Booking objects that have been held

```java
SeatsioClient client = new SeatsioClient(<SECRET KEY>);
client.events.book(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"), <A HOLD TOKEN>);
```

### Changing object status

```java
SeatsioClient client = new SeatsioClient(<SECRET KEY>);
client.events.changeObjectStatus(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"), "unavailable");
```

### Listing charts

```java
SeatsioClient client = new SeatsioClient(<SECRET KEY>);

Chart chart1 = client.charts.create();
Chart chart2 = client.charts.create();
Chart chart3 = client.charts.create();

Stream<Chart> charts = client.charts.listAll();
```

## Error handling

When an API call results in a 4xx or 5xx error (e.g. when a chart could not be found), a SeatsioException is thrown.

This exception contains a message string describing what went wrong, and also two other properties:

- `messages`: a list of error messages that the server returned. In most cases, this array will contain only one element.
- `requestId`: the identifier of the request you made. Please mention this to us when you have questions, as it will make debugging easier.
