# New features in Java 17

This page is about how YFinance4J library demonstrating new features after Java 8 till Java 17 (LTS). For usage of YFinance4J library, please read [README.md](./README.md).

## Introduction

An article titled [Java Platform Evolution](https://dev.java/evolution/) pointed out that Java 8 released in 2014 was one of the most popular version in Java's history and remained widely used even though the LTS version was Java 17 released in 2021.

Under a new release model since 2017, as a matter of fact, a new feature version of Java is released every 6 months. A bunch of new features have been introduced since Java 8. It is a good moment to review those which can help our productivity. The following features which seem commonly used are highlighted and explained by examples.

- [Local Variable Type Inference](#var-keyword)
- [Text Block](#text-block)
- [Record](#record-type)
- [Sealed Types](#sealed-types)
- [Switch Expression](#switch-expression)
- [Pattern Matching for instanceof](#pattern-instanceof)
- [Enhancement of Optional](#enhance-optional)
- [Collection Factory Methods](#collection-factory)
- [Stream API Collectors](#stream-api)
- [Package java.net.http](#net-http)

For a complete list of the Java Enhancement Proposals, please find in [JEPs in JDK 17 integrated since JDK 11](https://openjdk.org/projects/jdk/17/jeps-since-jdk-11).   

<a name="var-keyword"></a>

## Local Variable Type Inference

The `var` keyword was introduced in Java 10. The data type of a local variable can be automatically detected by the context.

An example in [HistoricalQuoteCli](./src/test/java/self/tekichan/demo/yfinance4j/example/HistoricalQuoteCli.java):
```java
var symbol = args[0];   // from String[] args arguments
var timeout = YFinanceConfig.TIMEOUT_MILLIS;    // defined as int in YFinanceConfig class
var startDate = LocalDate.of(1985, 12, 28);
```

The keyword `var` replace `String`, `int` and `LocalDate` respectively in the above example.

Even a more complicated type is applicable, e.g. in [HistoricalQuoteGrouping](./src/test/java/self/tekichan/demo/yfinance4j/example/HistoricalQuoteGrouping.java):
```java
var historicalQuoteList = YFinance4J.historicalQuoteCtrl()
        .symbol(symbol)
        .getHistoricalData();
var result = historicalQuoteList.stream()
        .collect(
            groupingBy(quote -> quote.tradeDate().getYear(),
                    filtering(quote -> quote.openPrice().compareTo(quote.closePrice()) < 0,
                            counting()
                    )
            )
        );
```
The variable `historicalQuoteList` is inferred as `List<HistoricalQuote>` and the variable `result` is inferred as `Map<Integer, Long>`.

<a name="text-block"></a>

## Text Block

Text block introduced in Java 15 brings us pain-free multi-line strings. It makes our Java codes more readable and less verbose.

Taking [HistoricalQuoteCli](./src/test/java/self/tekichan/demo/yfinance4j/example/HistoricalQuoteCli.java) as an example,
```java
return """
Usage - java %1$s symbol [-t timeout] [-s startdate] [-e enddate] [-i interval]
symbol: Stock code symbol. e.g: TSCO.L
timeout: Timeout in second. e.g: 1
startdate: Starting date in yyyy-MM-dd.
enddate: Ending date in yyyy-MM-dd.
interval: Reportinng interval. d: Daily, w: Weekly, m: Monthly
""".formatted(HistoricalQuoteCli.class.getName());
```
Text block starts with three double quotes and end with another three double quotes. We do not need plus sign to concatenate String anymore.

Another helpful usage is in making JSON:
```java
return """
{
  "trade_date": "%1$s"
  , "open": %2$s
  , "high": %3$s
  , "low": %4$s
  , "close": %5$s
  , "adj_close": %6$s
  , "volume": %7$s
}
""".formatted(
        DATE_FORMATTER.format(quote.tradeDate()),
        quote.openPrice().toString()
        , quote.highPrice().toString()
        , quote.lowPrice().toString()
        , quote.closePrice().toString()
        , quote.adjustPrice().toString()
        , quote.volume().toString()
    );
}
```

Well formatted strings can be generated as the following:
```
[{
  "trade_date": "2022-07-25"
  , "open": 259.000000
  , "high": 264.899994
  , "low": 258.299988
  , "close": 264.799988
  , "adj_close": 264.799988
  , "volume": 11235579
},{
    ...
},{
  "trade_date": "2022-08-01"
  , "open": 262.500000
  , "high": 264.399994
  , "low": 262.243195
  , "close": 263.399994
  , "adj_close": 263.399994
  , "volume": 806530
}]
```

<a name="record-type"></a>

## Record

Record as a new kind of type declaration was introduced in Java 14. It is a restricted form of a class. It creates an immutable data object.

For example, YFinance4J defines a record [HistoricalQuote](./src/main/java/self/tekichan/demo/yfinance4j/model/HistoricalQuote.java):
```java
public record HistoricalQuote(
        String symbol
        , LocalDate tradeDate
        , BigDecimal openPrice
        , BigDecimal highPrice
        , BigDecimal lowPrice
        , BigDecimal closePrice
        , BigDecimal adjustPrice
        , Long volume
) {
    // ...
}
```
Then an object of HistoricalQuote can be created by 
```java
var historicalQuote = new HistoricalQuote(
        "TSCO.L"
        , LocalDate.of(2022, 7, 25)
        , BigDecimal.valueOf(259.000000)
        , BigDecimal.valueOf(264.899994)
        , BigDecimal.valueOf(258.299988)
        , BigDecimal.valueOf(264.799988)
        , BigDecimal.valueOf(264.799988)
        , Long.valueOf(11235579)
        );
```

Record comes with a default constructor as shown in the above. It is also possible to define another constructor inside a record, e.g.
```java
public HistoricalQuote(String... args) {
    this(
            args[0]
            , LocalDate.parse(args[1], DATE_FORMATTER)
            , new BigDecimal(args[2])
            , new BigDecimal(args[3])
            , new BigDecimal(args[4])
            , new BigDecimal(args[5])
            , new BigDecimal(args[6])
            , Long.valueOf(args[7])
    );
}
```
such that it can be created as
```java
var historicalQuote = new HistoricalQuote(
        "TSCO.L"
        , "2022-07-25"
        , "259.000000"
        , "264.899994"
        , "258.299988"
        , "264.799988"
        , "264.799988"
        , "11235579"
        );
```

In addition to an alternative constructor, you can define a method inside record, e.g. in [SummaryQuote](./src/main/java/self/tekichan/demo/yfinance4j/model/SummaryQuote.java),
```java
public record SummaryQuote(
        // ...
) {
    public String getDescription() {
        return "%1$s - %2$s".formatted(this.symbol, this.companyName);
    }
}
```
Then you can call the method as usual:
```java
var summaryQuote = new SummaryQuote(...);
System.out.println(summaryQuote.getDescription());
```

<a name="sealed-types"></a>

## Sealed types

[Sealed types](https://docs.oracle.com/en/java/javase/17/language/sealed-classes-and-interfaces.html) introduced in Java 17 aim at providing more control about which other classes or interfaces may extend or implement them.

In the past, we can use `final` to prevent a class from being extended by others. All or nothing. With the new feature, we can assign which classes can extend a parent class or interface. For example, it is intended to let [SummaryQuote](./src/main/java/self/tekichan/demo/yfinance4j/model/SummaryQuote.java) and [KeyStatistics](./src/main/java/self/tekichan/demo/yfinance4j/model/KeyStatistics.java) to implement [IStockQuote](./src/main/java/self/tekichan/demo/yfinance4j/model/IStockQuote.java):
```java
public sealed interface IStockQuote permits SummaryQuote, KeyStatistics {
    // ...
}
```
and SummaryQuote and KeyStatistics can simply implement it while others cannot:
```java
public record KeyStatistics(
        // ...
) implements IStockQuote {
    // ...
}
```

<a name="switch-expression"></a>

## Switch Expression

A new syntax of `switch` expression introduced in Java 12 allows being used to evaluate to a single value and being used in statements.

For example in [HistoricalQuoteCtrl](./src/main/java/self/tekichan/demo/yfinance4j/ctrl/HistoricalQuoteCtrl.java):
```java
private String toIntervalCode(Interval interval) {
    return switch(interval) {
        case WEEKLY -> "1wk";
        case MONTHLY -> "1mo";
        default -> "1d";    // Default Daily
    };
}
```
The `switch` expression is used in a statement instead of being a statement. The value is given by an arrow instead of `return`. No `break` is needed.

If you want the value is given after another statement is run, it can be done with a keyword `yield`:
```java
return switch(interval) {
    case WEEKLY -> {
        System.out.println("Weekly Report");
        yield "1wk";
        }
    case MONTHLY -> {
        System.out.println("Monthly Report");
        yield "1mo";
        }
    default -> {
        System.out.println("Daily Report by default");
        yield "1d";
        }
};
```

<a name="pattern-instanceof"></a>

## Pattern Matching for instanceof

In the past, after using `instanceof` to check a variable's instance type, we had to explicitly convert it to the checked type.

Since Java 14, we can specify a binding variable via `instanceof`. For example in [InstanceOfDemo](./src/test/java/self/tekichan/demo/yfinance4j/example/InstanceOfDemo.java):
```java
if (info instanceof SummaryQuote sq) {
    System.out.println(sq.symbol());
    System.out.println("Day Range: %1$f - %2$f".formatted(sq.dayLow(), sq.dayHigh()));
} else if (info instanceof KeyStatistics ks) {
    System.out.println(ks.companyName());
    System.out.println("50-day Moving Average: %1$f".formatted(ks.fiftyDayMovingAverage()));
}
```
The variable **info** are converted to its subtypes **sg** and **ks** respectively via `instanceof`. 

<a name="enhance-optional"></a>

## Enhancement of Optional

Since Java 9, there are more new methods introduced in [Optional](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Optional.html) to support functional programming. 

- `ifPresentOrElse(Consumer<? super T>, Runnable)`: Perform the given action with the value if a value is present, otherwise performs the given empty-based action.
- `or(Supplier<Optional<T>>supplier)`: Return an Optional describing the value if a value is present, otherwise returns an Optional produced by the supplying function.
- `stream()`: Return a sequential Stream containing only that value if a value is present, otherwise returns an empty Stream.
- `orElseThrow()`: Return the value if a value is present, otherwise throws NoSuchElementException.
- `isEmpty()`: (since Java 11) Return true if a value is present, otherwise false.

For example, [HistoricalQuoteCtrl](./src/main/java/self/tekichan/demo/yfinance4j/ctrl/HistoricalQuoteCtrl.java) uses `or` to simplify returning a default value if a parameter is not present:
```java
public HistoricalQuoteCtrl startDate(LocalDate startDate) {
    this.startDateEpoch = Optional.ofNullable(startDate)
            .map(d -> d.toEpochSecond(LocalTime.of(0, 0, 0), ZoneOffset.UTC))
            .or(() -> Optional.of(START_EPOCH));
    return this;
}
```

<a name="collection-factory"></a>

## Collection Factory Methods

Since Java 9, factory methods of collections are provided to simplify collection creation (without calling `add` again and again):

- `List.of`
- `Set.of`
- `Map.of`
- `Map.ofEntries`

For example in [YFinance4JTest](./src/test/java/self/tekichan/demo/yfinance4j/YFinance4JTest.java):
```java
static final List<String> INDEX_LIST = List.of( "^FTSE", "^NYA", "^IXIC", "^HSI", "^NSEI" );
```

<a name="stream-api"></a>

## Stream API Collectors

Since Java 9, more new methods are present to support and simplify data grouping.

- `Collectors::filtering`: Filter elements in `groupingBy`
- `Collectors::flatMapping`: Apply a flat mapping to each element before accumulation in `groupingBy`. 
- `Collectors::teeting`: (since Java 12) Return a `Collector` object which aggregates the results of two downstream collectors.
- `Stream::toList`: (since Java 16) replace `Stream::collect(Collectors.toList())`

Taking an example in [HistoricalQuoteGrouping](./src/test/java/self/tekichan/demo/yfinance4j/example/HistoricalQuoteGrouping.java):
```java
var result = historicalQuoteList.stream()
        .collect(
            groupingBy(quote -> quote.tradeDate().getYear(),
                    filtering(quote -> quote.openPrice().compareTo(quote.closePrice()) < 0,
                            counting()
                    )
            )
        );
```
The variable `historicalQuoteList` is a `List` of `HistoricalQuote`. After the grouping by year operation with filtering open price lower than close price (i.e. bullish day), it returns a result as `Map<Integer, Long>`.

The sample output of the program:
```
For 0005.HK, bullish days count per year:
-*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*--*-
{2016=123, 2017=140, 2018=126, 2019=122, 2020=120, 2021=123, 2022=82, 2000=120, 2001=116, 2002=118, 2003=120, 2004=114, 2005=97, 2006=115, 2007=124, 2008=107, 2009=129, 2010=133, 2011=124, 2012=134, 2013=115, 2014=128, 2015=124}
```

[HistoricalQuoteCtrl](./src/main/java/self/tekichan/demo/yfinance4j/ctrl/HistoricalQuoteCtrl.java) is benefited by `Stream::toList` to simplify obtaining a `List` result.

```java
private List<HistoricalQuote> fromStreamToList(Stream<String> lines) {
    return lines.skip(1)
            .map(line -> {
                try {
                    String[] lineItems = line.split(",");
                    if (this.symbol.isPresent() && lineItems.length >= 7) {
                        return new HistoricalQuote(
                                this.symbol.get()
                                , lineItems[0]
                                , lineItems[1]
                                , lineItems[2]
                                , lineItems[3]
                                , lineItems[4]
                                , lineItems[5]
                                , lineItems[6]
                        );
                    } else {
                        return null;
                    }
                } catch(Exception ex) {
                    return null;
                }
            })
            .filter(item -> item != null)
            .sorted(Comparator.comparing(HistoricalQuote::tradeDate))
            .toList();
}
```

<a name="net-http"></a>

## Package java.net.http

Java 11 introduced a new package [java.net.http](https://docs.oracle.com/en/java/javase/17/docs/api/java.net.http/java/net/http/package-summary.html) to replace the legacy [HttpURLConnection](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/net/HttpURLConnection.html), which does not seem feature-rich and user-friendly. Before that, third-party libraries, such as Apache HttpClient, Jetty and Spring's RestTemplate, were widely used.

The new package provides native support for HTTP (versions 1.1 and 2) and WebSocket. The main types defined include:
- `HttpClient`: used to send requests and receive their responses.
- `HttpRequest`: represent an HTTP request with URI, headers and body.
- `HttpResponse`: represent an HTTP response with status code, headers and body.
- `WebSocket`: act as a WebSocket client.

[WebClientHelper](./src/main/java/self/tekichan/demo/yfinance4j/util/WebClientHelper.java) as an example shows how to create HttpRequest, HttpClient and HttpResponse.

A method to build HttpRequest:
```java
public static HttpRequest buildHttpRequest(String targetUrl, int timeoutMillis)
            throws URISyntaxException {
    return HttpRequest.newBuilder()
            .uri(new URI(targetUrl))
            .timeout(Duration.of(timeoutMillis, MILLIS))
            .GET()
            .build();
}
```

A method to build HttpClient:
```java
public static HttpClient buildHttpClient(int timeoutMillis) {
    return HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.of(timeoutMillis, MILLIS))
            .build();
}
```

A method to receive HttpResponse with a synchronous call:
```java
public static HttpResponse<String> getHttpResponse(String targetUrl, int timeoutMillis)
        throws IOException, InterruptedException, URISyntaxException {
    return buildHttpClient(timeoutMillis)
            .send(
                    buildHttpRequest(targetUrl, timeoutMillis)
                    , HttpResponse.BodyHandlers.ofString()
            );
}
```

A method to receive HttpResponse with an asynchronous call:
```java
public static CompletableFuture<HttpResponse<String>> getHttpResponseAsync(String targetUrl, int timeoutMillis)
            throws URISyntaxException {
    return buildHttpClient(timeoutMillis)
            .sendAsync(
                    buildHttpRequest(targetUrl, timeoutMillis)
                    , HttpResponse.BodyHandlers.ofString()
            );
}
```

## Author

- Teki Chan *[tekichan@gmail.com](mailto:tekichan@gmail.com)*
