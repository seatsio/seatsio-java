# seatsio-java, the official seats.io Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/io.seats/seatsio-java)](https://central.sonatype.dev/namespace/io.seats)
[![Build Status](https://img.shields.io/github/actions/workflow/status/seatsio/seatsio-java/build.yml)](https://github.com/seatsio/seatsio-java/actions/workflows/build.yml)

## Installing seatsio-java

seatsio-java is available in the Maven Central repository:

```
// build.gradle
dependencies {
  compile 'io.seats:seatsio-java:87.7.0'
}

// pom.xml
<dependency>
  <groupId>io.seats</groupId>
  <artifactId>seatsio-java</artifactId>
  <version>87.7.0</version>
</dependency>
```

Note that v74.0.0 is the first version that's hosted on Maven Central instead of on JitPack.

## Java version

You need at least Java 11 to use seatsio-java.

## Versioning

seatsio-java follows semver since v52.2.0.

## Usage

### General instructions

To use this library, you'll need to create a `SeatsioClient`:

```java
import seatsio.SeatsioClient;
import seatsio.Region;

SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
...
```

You can find your _workspace secret key_ in the [settings section of the workspace](https://app.seats.io/workspace-settings).

The region should correspond to the region of your account:

- `Region.EU`: Europe
- `Region.NA`: North-America
- `Region.SA`: South-America
- `Region.OC`: Oceania

If you're unsure about your region, have a look at your [company settings page](https://app.seats.io/company-settings).

### Creating a chart and an event

```java
import seatsio.SeatsioClient;
import seatsio.Region;
import seatsio.charts.Chart;
import seatsio.events.Event;

SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
Chart chart = client.charts.create();
Event event = client.events.create(chart.key);
System.out.println("Created event with key " + event.key);
```

### Booking objects

```java
import seatsio.SeatsioClient;
import seatsio.Region;
import seatsio.events.Event;

SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
client.events.book(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"));
```

### Releasing objects

```java
import seatsio.SeatsioClient;
import seatsio.Region;

SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
client.events.release(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"));
```

### Booking objects that have been held

```java
import seatsio.SeatsioClient;
import seatsio.Region;

SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
client.events.book(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"), <A HOLD TOKEN>);
```

### Changing object status

```java
import seatsio.SeatsioClient;
import seatsio.Region;

SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>);
client.events.changeObjectStatus(<AN EVENT KEY>, Arrays.asList("A-1", "A-2"), "unavailable");
```

### Listing all charts

```java
import seatsio.SeatsioClient;
import seatsio.Region;
import seatsio.charts.Chart;

SeatsioClient client = new SeatsioClient(Region.EU, "<WORKSPACE SECRET KEY>");

Stream<Chart> charts = client.charts.listAll();
charts.forEach(chart -> {
  System.out.println("Chart " + chart.key);
});
```

Note: `listAll()` returns a stream, which under the hood calls the seats.io API to fetch charts page by page. So multiple API calls may be done underneath to fetch all charts.

### Retrieving object category and status (and other information)

```java
import seatsio.SeatsioClient;
import seatsio.Region;
import seatsio.events.EventObjectInfo;

SeatsioClient client = new SeatsioClient(Region.EU, "<WORKSPACE SECRET KEY>");

Map<String, EventObjectInfo> eventObjectInfos = client.events.retrieveObjectInfos(event.key, List.of("A-1", "A-2"));

System.out.println(eventObjectInfos.get("A-1").categoryLabel);
System.out.println(eventObjectInfos.get("A-1").categoryKey);
System.out.println(eventObjectInfos.get("A-1").status);

System.out.println(eventObjectInfos.get("A-2").categoryLabel);
System.out.println(eventObjectInfos.get("A-2").categoryKey);
System.out.println(eventObjectInfos.get("A-2").status);
```

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
import seatsio.SeatsioClient;
import seatsio.Region;

SeatsioClient client = new SeatsioClient(Region.EU, <COMPANY ADMIN KEY>); // company admin key can be found on https://app.seats.io/company-settings
client.workspaces.create("a workspace");
```

### Creating a chart and an event with the company admin key

```java
import seatsio.SeatsioClient;
import seatsio.Region;
import seatsio.charts.Chart;
import seatsio.events.Event;

// company admin key can be found on https://app.seats.io/company-settings
// workspace public key can be found on https://app.seats.io/workspace-settings
SeatsioClient client = new SeatsioClient(Region.EU, <COMPANY ADMIN KEY>, <WORKSPACE PUBLIC KEY>);
Chart chart = client.charts.create();
Event event = client.events.create(chart.key);
System.out.println("Created event with key " + event.key);
```

### Listing categories

```java
import seatsio.SeatsioClient;
import seatsio.Category;

SeatsioClient client = new SeatsioClient(Region.EU, "<WORKSPACE SECRET KEY>");

List<Category> categories = client.charts.listCategories("the chart key");
categories.forEach(category -> {
  System.out.println("Category " + category.label);
});
```

### Updating a category
```java
import seatsio.charts.CategoryKey;
import seatsio.charts.CategoryUpdateParams;
import seatsio.SeatsioClient;

SeatsioClient client = new SeatsioClient(Region.EU, "<WORKSPACE SECRET KEY>");

client.charts.updateCategory("the chart key", CategoryKey.of("the category key"),
  new CategoryUpdateParams("New label", "#cccccc", false));
```

## Error handling

When an API call results in a 4xx or 5xx error (e.g. when a chart could not be found), a SeatsioException is thrown.

This exception contains a message string describing what went wrong, and also two other properties:

- `errors`: a list of errors that the server returned. In most cases, this array will contain only one element, an instance of ApiError, containing an error code and a message.
- `requestId`: the identifier of the request you made. Please mention this to us when you have questions, as it will make debugging easier.

## Rate limiting - exponential backoff

This library supports [exponential backoff](https://en.wikipedia.org/wiki/Exponential_backoff).

When you send too many concurrent requests, the server returns an error `429 - Too Many Requests`. The client reacts to this by waiting for a while, and then retrying the request.
If the request still fails with an error `429`, it waits a little longer, and try again. By default this happens 5 times, before giving up (after approximately 15 seconds).

We throw a `RateLimitExceededException` (which is a subclass of `SeatsioException`) when exponential backoff eventually fails.

To change the maximum number of retries, create the `SeatsioClient` as follows:

```java
SeatsioClient client = new SeatsioClient(Region.EU, <WORKSPACE SECRET KEY>).maxRetries(3);
```

Passing in 0 disables exponential backoff completely. In that case, the client will never retry a failed request.

## Multi-threading

It's perfectly fine to re-use an instance of `SeatsioClient` in different threads. All methods are thread-safe.

## Connection pooling

We manage a connection pool under the hood, to avoid having to reconnect to the seats.io server on every call. This pool supports 200 concurrent connections.

The connection pool is shared between all instances of `SeatsioClient`.

In some cases, e.g. when you're running automated test suite against a seats.io stub, it may be useful to reset the connection pool between tests. 
To do so, call `seatsioClient.reinitializeHttpConnectionPool()`.  
