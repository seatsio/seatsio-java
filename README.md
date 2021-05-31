# seatsio-java, the official Seats.io Java client library

[![Build](https://github.com/seatsio/seatsio-java/workflows/Build/badge.svg)](https://github.com/seatsio/seatsio-java/actions/workflows/build.yml)
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
  compile 'com.github.seatsio:seatsio-java:64.5.0'
}

// pom.xml
<dependency>
  <groupId>com.github.seatsio</groupId>
  <artifactId>seatsio-java</artifactId>
  <version>64.5.0</version>
</dependency>
```

## Java version

You need at least Java 8 (with update 101) to use seatsio-java.

## Versioning

seatsio-java follows semver since v52.2.0.

## Examples

### Creating a chart and an event

```java
SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>); // secret key can be found on https://app.seats.io/workspace-settings
Chart chart = client.charts.create();
Event event = client.events.create(chart.key);
System.out.println("Created event with key " + event.key);
```

### Booking objects

```java
SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
client.events.book(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"));
```

### Releasing objects

```java
SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
client.events.release(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"));
```

### Booking objects that have been held

```java
SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
client.events.book(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"), <A HOLD TOKEN>);
```

### Changing object status

```java
SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
client.events.changeObjectStatus(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"), "unavailable");
```

### Listing all charts

```java
SeatsioClient client = new SeatsioClient(Region.EU, "<WORKSPACE SECRET KEY>");

Stream<Chart> charts = client.charts.listAll();
charts.forEach(chart -> {
  System.out.println("Chart " + chart.key);
});
```

Note: `listAll()` returns a stream, which under the hood calls the seats.io API to fetch charts page by page. So multiple API calls may be done underneath to fetch all charts.

### Listing charts page by page

E.g. to show charts in a paginated list on a dashboard.

Each page contains an `items` array of charts, and `nextPageStartsAfter` and `previousPageEndsBefore` properties. Those properties are the chart IDs after which the next page starts or the previous page ends.

```java
// ... user initially opens the screen ...

Page<Chart> firstPage = client.charts.listFirstPage();
for(Chart chart: charts.items) {
  System.out.println("Chart " + chart.key)
}
```

```java
// ... user clicks on 'next page' button ...

Page<Chart> nextPage = client.charts.listPageAfter(firstPage.nextPageStartsAfter);
for(Chart chart: charts.items) {
  System.out.println("Chart " + chart.key)
}
```

```java
// ... user clicks on 'previous page' button ...

Page<Chart> previousPage = client.charts.listPageBefore(nextPage.previousPageEndsBefore);
for(Chart chart: charts.items) {
  System.out.println("Chart " + chart.key)
}
```

### Creating a workspace

```java
SeatsioClient client = new SeatsioClient(Region.EU, <COMPANY ADMIN KEY>);
client.workspaces.create("a workspace");
```

## Error handling

When an API call results in a 4xx or 5xx error (e.g. when a chart could not be found), a SeatsioException is thrown.

This exception contains a message string describing what went wrong, and also two other properties:

- `errors`: a list of errors that the server returned. In most cases, this array will contain only one element, an instance of ApiError, containing an error code and a message.
- `requestId`: the identifier of the request you made. Please mention this to us when you have questions, as it will make debugging easier.

## Rate limiting - exponential backoff

This library supports [exponential backoff](https://en.wikipedia.org/wiki/Exponential_backoff).

When you send too many concurrent requests, the server returns an error `429 - Too Many Requests`. The client reacts to this by waiting for a while, and then retrying the request.
If the request still fails with an error `429`, it waits a little longer, and try again. This happens at most 5 times, before giving up (after approximately 15 seconds).
