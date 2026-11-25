# seatsio-java, the official seats.io Java SDK

[![Maven Central](https://img.shields.io/maven-central/v/io.seats/seatsio-java)](https://central.sonatype.dev/namespace/io.seats)
[![Build Status](https://img.shields.io/github/actions/workflow/status/seatsio/seatsio-java/build.yml)](https://github.com/seatsio/seatsio-java/actions/workflows/build.yml)

## Installing seatsio-java

seatsio-java is available in the Maven Central repository:

```
// build.gradle
dependencies {
  compile 'io.seats:seatsio-java:91.8.0'
}

// pom.xml
<dependency>
  <groupId>io.seats</groupId>
  <artifactId>seatsio-java</artifactId>
  <version>91.8.0</version>
</dependency>
```

Note that v74.0.0 is the first version that's hosted on Maven Central instead of on JitPack.

## Java version

You need at least Java 17 to use seatsio-java.

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
Event event = client.events.create(chart.key());
System.out.println("Created event with key " + event.key());
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
  System.out.println("Chart " + chart.key());
});
```

Note: `listAll()` returns a stream, which under the hood calls the seats.io API to fetch charts page by page. So multiple API calls may be done underneath to fetch all charts.

### Retrieving object category and status (and other information)

```java
import seatsio.SeatsioClient;
import seatsio.Region;
import seatsio.events.EventObjectInfo;

SeatsioClient client = new SeatsioClient(Region.EU, "<WORKSPACE SECRET KEY>");

Map<String, EventObjectInfo> eventObjectInfos = client.events.retrieveObjectInfos(event.key(), List.of("A-1", "A-2"));

System.out.println(eventObjectInfos.get("A-1").categoryLabel());
System.out.println(eventObjectInfos.get("A-1").categoryKey());
System.out.println(eventObjectInfos.get("A-1").status());

System.out.println(eventObjectInfos.get("A-2").categoryLabel());
System.out.println(eventObjectInfos.get("A-2").categoryKey());
System.out.println(eventObjectInfos.get("A-2").status());
```

### Listing charts page by page

E.g. to show charts in a paginated list on a dashboard.

Each page contains an `items` array of charts, and `nextPageStartsAfter` and `previousPageEndsBefore` properties. Those properties are the chart IDs after which the next page starts or the previous page ends.

```java
// ... user initially opens the screen ...

Page<Chart> firstPage = client.charts.listFirstPage();
for(Chart chart: charts.items) {
  System.out.println("Chart " + chart.key())
}
```

```java
// ... user clicks on 'next page' button ...

Page<Chart> nextPage = client.charts.listPageAfter(firstPage.nextPageStartsAfter);
for(Chart chart: charts.items) {
  System.out.println("Chart " + chart.key())
}
```

```java
// ... user clicks on 'previous page' button ...

Page<Chart> previousPage = client.charts.listPageBefore(nextPage.previousPageEndsBefore);
for(Chart chart: charts.items) {
  System.out.println("Chart " + chart.key())
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
Event event = client.events.create(chart.key());
System.out.println("Created event with key " + event.key());
```

### Listing categories

```java
import seatsio.SeatsioClient;
import seatsio.Category;

SeatsioClient client = new SeatsioClient(Region.EU, "<WORKSPACE SECRET KEY>");

List<Category> categories = client.charts.listCategories("the chart key");
categories.forEach(category -> {
  System.out.println("Category " + category.label());
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

This exception contains a message string describing what went wrong, and also these properties:

- `errors`: a list of errors that the server returned. In most cases, this array will contain only one element, an instance of ApiError, containing an error code and a message.
- `requestId`: the identifier of the request you made. Please mention this to us when you have questions, as it will make debugging easier.
- `idempotencyKey`: the `Idempotency-Key` sent with a `POST` or `DELETE` request (see [Idempotency](#idempotency)), or `null` for other requests.
- `idempotencyKeyHonored`: whether the server honored that key, according to the `Idempotency-Key-Status` header of the response; `null` when there was no response or the response didn't say.
- `replayed`: `true` when the server didn't execute the request again, but replayed the response of an earlier request with the same key.

`outcomeIsUnknown()` tells you whether the request may or may not have been executed: it's `true` when the response was lost after the request was sent (a timeout, a connection reset), when the server answered with a 5xx, or when it answered `409` with error code `IDEMPOTENCY_KEY_IN_USE` after the client ran out of retries (an earlier request with the same key was still being processed). It's `false` when the request never reached the server (connection refused, DNS or TLS failure) and for every other 4xx.

`isSafeToRetryWithSameKey()` tells you whether you can make the same call again with the same key (see [Idempotency](#idempotency)) without risking executing it twice, and whether that could give a different result.

If the server returns an error response without an error code (for example, an empty `errors` array), `errors` is empty, `requestId` is still set if the response contains one, and the message contains the HTTP status and the response body. If the response body is not a JSON object, `errors` and `requestId` are `null`.

## Idempotency

**NOTE**: Idempotency is currently in a soft launch: it is enabled per company and not yet publicly available. Contact Seats.io support to have it enabled for your company.

Every `POST` and `DELETE` request is sent with an `Idempotency-Key` header (a v4 UUID, generated by the client). If idempotency is enabled for your company, the server executes a request with a given key only once, and replays the original response when it receives the same request again. The server keeps a key for 10 minutes.

Each response to a `POST` or `DELETE` says what the server did with the key, in the `Idempotency-Key-Status` header: `executed`, `replayed`, `in-use` (an earlier request with the same key is still being processed), `reused` (the key was used for a different request) or `ignored` (idempotency isn't enabled, so the server executed the request without looking at the key).

If the connection fails after the server executed a request but before you received the response, you don't know whether the operation succeeded. The exception contains the key, and tells you whether retrying with that key is safe:

```java
try {
    client.events.create(chartKey);
} catch (SeatsioException e) {
    // true when the request never reached the server, or when its outcome is unknown (a lost response, a 5xx,
    // a 409 IDEMPOTENCY_KEY_IN_USE) and the server honors idempotency keys for it
    if (e.isSafeToRetryWithSameKey()) {
        client.withIdempotencyKey(e.idempotencyKey).events.create(chartKey);
    }
}
```

Retry promptly: after 10 minutes, the server has forgotten the key. The retried request must be identical to the original one, otherwise the server returns an error `422`. Every `POST` and `DELETE` made with the client returned by `withIdempotencyKey()` sends that key, so use it for one operation only. You can also supply your own key; it must be a v4 UUID, otherwise `withIdempotencyKey()` throws an `IllegalArgumentException`.

### Automatic retries

The client retries a `POST` or `DELETE` with the same key:

- when the server answers `429`, or `409` with error code `IDEMPOTENCY_KEY_IN_USE`;
- when the request never reached the server (connection refused, connect timeout, DNS or TLS failure);
- when the response was lost after the request was sent, but only if the server has confirmed that it honors idempotency keys for that operation. The client remembers the `Idempotency-Key-Status` of the last response for each operation (for example "book objects in an event", whatever the event), so the first call of an operation is never retried after a lost response, and calls of an operation that answered `ignored` aren't either.

All these retries count against `maxRetries()`. Once a request may have reached the server, the client doesn't start a retry more than 5 minutes after the first attempt, well within the lifetime of the key. A `422` and an invalid key are never retried. The clients returned by `withIdempotencyKey()` share the settings and the memory of the client they were made from.

Because of that memory, create one `SeatsioClient` and share it across your application (it's thread-safe), instead of creating a new one per request. A client that is created per request starts with an empty memory, so it never retries after a lost response, and idempotency can't protect you from executing an operation twice.

There is a small remaining risk: if idempotency is switched off for your company between the last response that said an operation honors keys and a lost response, the retry can execute the operation a second time. The next response with `ignored` corrects the client's memory, so this window is short.

The client stops retrying when its thread is interrupted (the interrupt flag stays set). This applies to all retries, including those after a `429`.

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
