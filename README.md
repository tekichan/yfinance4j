# YFinance4J
[![License](https://img.shields.io/badge/license-MIT-green.svg)](/LICENSE)

Java Library for Yahoo! Finance

## Introduction

YFinance4J is a Java library to retrieve stock related quotes and information from [Yahoo! Finance](https://finance.yahoo.com/). The information includes historical quotes and key statistics. The data source is from Yahoo! Finance US websites. Stocks on the US stock exchanges usually have more data available than stocks on other countries. Because of Yahoo! Finance as the data source, Terms of Service should be referred to [Yahoo Terms of Service](https://legal.yahoo.com/us/en/yahoo/terms/otos/index.html).

Actually this project aims at demonstrating key features of Java 17. Anyone is welcomed to fork this project and encouraged to enhance this with advanced and up-to-date Java features.

### Acknowledgement

This project is inspired by [Quotes API for Yahoo Finance](https://financequotes-api.com/) and [YFinance](https://github.com/ranaroussi/yfinance). Thanks a lot for their efforts.

### Disclaimer

This project is not associated with nor sponsored by Yahoo! Inc. Yahoo! Inc. is the exclusive owner of all trademark and other intellectual property rights in and to the YAHOO! and Y! trademarks (the "Trademarks"), including the stylized YAHOO! and Y! logos. Yahoo! Inc. owns trademark registrations for the Trademarks.

## Library compilation

This project is built with Gradle. JAR file is simply built with `./gradlew clean build`.

The JAR file is bundled with dependent libraries. If you want to know the dependency detail, see [build.gradle.kts](./build.gradle.kts).

### Build Requirements

- JDK 17
- Gradle 7.3

## Functions

All functions are started with `YFinance4J`. There are main functions as the following:

- [Historical Quote](#historical-quote)
- [Index Component](#index-component)
- [Key Statistics](#key-statistics)
- [Summary Quote](#summary-quote)

<a name="historical-quote"></a>

### Historical Quote

Historical Quote is a list of historical price records of a stock. Each element of the list consists of stock code, trading date, price data and volume.

For example in [HistoricalQuoteEg](./src/test/java/self/tekichan/demo/yfinance4j/example/HistoricalQuoteEg.java),
```java
List<HistoricalQuote> historicalQuoteList = YFinance4J.historicalQuoteCtrl()
        .symbol(symbol)
        .timeout(timeout * 1000)
        .startDate(startDate)
        .endDate(endDate)
        .interval(interval)
        .getHistoricalData();
System.out.println("[" +
                String.join(
                        ",",
                        historicalQuoteList.stream()
                                .map(quote -> """
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
)
                                )
                                .toList()
                ) + "]"
);
```
Sample output of the above is
```json
[{
  "trade_date": "1999-04-28"
  , "open": 237.499893
  , "high": 237.499893
  , "low": 233.383194
  , "close": 236.549896
  , "adj_close": 105.930550
  , "volume": 27608281
}
...
,{
  "trade_date": "2022-07-26"
  , "open": 260.000000
  , "high": 262.100006
  , "low": 257.200012
  , "close": 258.700012
  , "adj_close": 258.700012
  , "volume": 34679398
}]
```

The function supports asynchronous call.
```java
CompletableFuture<List<HistoricalQuote>> resultFuture = YFinance4J.historicalQuoteCtrl()
                            .symbol(testSymbol)
                            .getHistoricalDataAsync();
List<HistoricalQuote> result = null;
try {
    result = resultFuture.get();
} catch (InterruptedException iex) {
    iex.printStackTrace();
} catch (ExecutionException eex) {
    eex.printStackTrace();
}
```

<a name="index-component"></a>

### Index Component

Index Component is the information of component stocks of a stock index, e.g. ^FTSE represents [The Financial Times Stock Exchange 100 Index](https://en.wikipedia.org/wiki/FTSE_100_Index). Remark: Yahoo! Finance may not show a complete list of component stock of an index.

[IndexComponentPe](./src/test/java/self/tekichan/demo/yfinance4j/example/IndexComponentPe.java) shows an example how to retrieve index component stocks:
```java
var componentList = YFinance4J.indexComponentCtrl()
                .symbol(symbol)
                .getIndexComponentInfo()
                .componentList();
```

`IndexComponent` fields are described as the below:

Field | Description
--- | ---
symbol | Stock symbol
companyName | Company name
lastPrice | Last price
change | Price change
percentChange | Price change percentage
volume | Volume

<a name="key-statistics"></a>

### Key Statistics

Key Statistics is the statistics of a stock. The data consists of Valuation Measures, Financial Highlights and Trading Information. Remark: The statistics data are provided by Yahoo! Finance. The accuracy is subject to Yahoo! Finance. The data may not be timely.

[InstanceOfDemo](./src/test/java/self/tekichan/demo/yfinance4j/example/InstanceOfDemo.java) shows an example how to retrieve a set of key statistics.
```java
IStockQuote info2 = YFinance4J.keyStatisticsCtrl().symbol(symbol).getKeyStatistics();
```

`KeyStatistics` fields are described as the below:

Field | Description
--- | ---
symbol | Stock symbol
downloadDateTime | Date/Time of downloading the key statistics
companyName | Company Name
stockExchange | Stock exchange (abbr)
currencyCode | Currency code used by the stock
marketCap | Market Cap (intraday)
enterpriseValue | Enterprise Value
trailingPe | Trailing P/E
forwardPe | Forward P/E
pegRatio | PEG Ratio (5 yr expected)
priceSalesRatio | Price/Sales (Trailing Twelve Months)
priceBookRatio | Price/Book (Most Recent Quarter)
enterpriseValueRevenueRatio | Enterprise Value/Revenue
enterpriseValueEbitaRatio | Enterprise Value/EBITDA
fiscalYearEnds | Fiscal Year Ends
mostRecentQuarter | Most Recent Quarter (Most Recent Quarter)
profitMarginPercent | Profit Margin
operatingMarginPercent | Operating Margin (Trailing Twelve Months)
returnOnAssetsPercent | Return on Assets (Trailing Twelve Months)
returnOnEquityPercent | Return on Equity (Trailing Twelve Months)
revenue | Revenue (Trailing Twelve Months)
revenuePerShare | Revenue Per Share (Trailing Twelve Months)
quarterlyRevenueGrowthPercent | Quarterly Revenue Growth (Year Over Year)
grossProfit | Gross Profit (Trailing Twelve Months)
ebitda | EBITDA
netIncomeAviToCommon | Net Income Avi to Common (Trailing Twelve Months)
dilutedEps | Diluted EPS (Trailing Twelve Months)
quarterlyEarningsGrowthPercent | Quarterly Earnings Growth (Year Over Year)
totalCash | Total Cash (Most Recent Quarter)
totalCashPerShare | Total Cash Per Share (Most Recent Quarter)
totalDebt | Total Debt (Most Recent Quarter)
totalDebtEquityRatio | Total Debt/Equity (Most Recent Quarter)
currentRatio | Current Ratio (Most Recent Quarter)
bookValuePerShare | Book Value Per Share (Most Recent Quarter)
operatingCashFlow | Operating Cash Flow (Trailing Twelve Months)
leveredFreeCashFlow | Levered Free Cash Flow (Trailing Twelve Months)
beta | Beta (5Y Monthly)
fiftyTwoWeekChangePercent | 52-Week Change (Data derived from multiple sources or calculated by Yahoo Finance.)
snp50052WeekChangePercent | SnP500 52-Week Change (Data derived from multiple sources or calculated by Yahoo Finance.)
fiftyTwoWeekHigh | 52 Week High (Data derived from multiple sources or calculated by Yahoo Finance.)
fiftyTwoWeekLow | 52 Week Low (Data derived from multiple sources or calculated by Yahoo Finance.)
fiftyDayMovingAverage | 50-Day Moving Average (Data derived from multiple sources or calculated by Yahoo Finance.)
twoHundredDayMovingAverage | 200-Day Moving Average (Data derived from multiple sources or calculated by Yahoo Finance.)
avgVol3Month | Avg Vol (3 month) (Data derived from multiple sources or calculated by Yahoo Finance.)
avgVol10Day | Avg Vol (10 day) (Data derived from multiple sources or calculated by Yahoo Finance.)
sharesOutstanding | Shares Outstanding (Shares outstanding is taken from the most recently filed quarterly or annual report and Market Cap is calculated using shares outstanding.)
impliedSharesOutstanding | Implied Shares Outstanding (Implied Shares Outstanding of common equityassuming the conversion of all convertible subsidiary equity into common.)
sharesFloat | Float (A company's float is a measure of the number of shares available for trading by the public. It's calculated by taking the number of issued and outstanding shares minus any restricted stockwhich may not be publicly traded.)
heldByInsidersPercent | Percentage Held by Insiders
heldByInstitutions | Percentage Held by Institutions
sharesShort | Shares Short
shortRatio | Short Ratio
shortPercentOfFloat | Short percentage of Float
sharesOutstandingShortPercent | Short percentage of Shares Outstanding
sharesShortPriorMonth | Shares Short Prior Month
forwardAnnualDividendRate | Forward Annual Dividend Rate
forwardAnnualDividendYieldPercent | Forward Annual Dividend Yield
trailingAnnualDividendRate | Trailing Annual Dividend Rate (Data derived from multiple sources or calculated by Yahoo Finance.)
trailingAnnualDividendYieldPercent | Trailing Annual Dividend Yield (Data derived from multiple sources or calculated by Yahoo Finance.)
fiveYearAverageDividendYieldPercent | 5 Year Average Dividend Yield
payoutRatioPercent | Payout Ratio
dividendDate | Dividend Date (Data derived from multiple sources or calculated by Yahoo Finance.)
exDividendDate | Ex-Dividend Date
lastSplitFactor | Last Split Factor
lastSplitDate | Last Split Date

<a name="summary-quote"></a>

### Summary Quote

Summary Quote consists of the day's price information and abstracted statistics of a stock. Remark: The statistics data are provided by Yahoo! Finance. The accuracy is subject to Yahoo! Finance. The data may not be timely.

[InstanceOfDemo](./src/test/java/self/tekichan/demo/yfinance4j/example/InstanceOfDemo.java) shows an example how to retrieve a set of summary quote.
```java
IStockQuote info1 = YFinance4J.summaryQuoteCtrl().symbol(symbol).getSummaryQuote();
```

`SummaryQuote` fields are described as the below:

Field | Description
--- | ---
symbol | Stock symbol
downloadDateTime | Date/Time of downloading the summary
companyName | Company Name
stockExchange | Stock exchange (abbr)
currencyCode | Currency code used by the stock
previousPrice | Previous price
openPrice | Open price
bidPrice | Bid price
bidCount | Number of bid
askPrice | Ask price
askCount | Number of ask
dayLow | Lowest price on the day
dayHigh | Highest price on the day
fiftyTwoWeekLow | Lowest price in the 52 weeks
fiftyTwoWeekHigh | Highest price in the 52 weeks
volume | Volume
averageVolume | Average volume
marketCap | Market Cap (intraday)
beta | Beta (5Y Monthly)
peRatio | P/E ratio (Trailing Twelve Months)
eps | Earning per share (Trailing Twelve Months)
earningsDate | Earning date
forwardDividend | Forward dividend
forwardYieldPercent | Forward yield percentage
exDividendDate | Ex-Dividend date
oneYearEst | One year estimation

## Documentation

All main source codes are documented. Javadoc can be generated by
```shell
javadoc -protected -splitindex -d ./javadoc
```

## New Features in Java 17

This library also serves as an example of demonstrating new features of Java 17. For detail, see [JAVA_17_FEATURES.md](./JAVA_17_FEATURES.md).

## Author

- Teki Chan *[tekichan@gmail.com](mailto:tekichan@gmail.com)*
