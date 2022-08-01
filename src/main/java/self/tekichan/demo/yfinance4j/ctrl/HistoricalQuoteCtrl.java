package self.tekichan.demo.yfinance4j.ctrl;

import self.tekichan.demo.yfinance4j.model.HistoricalQuote;
import self.tekichan.demo.yfinance4j.model.Interval;
import self.tekichan.demo.yfinance4j.util.WebClientHelper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static self.tekichan.demo.yfinance4j.YFinanceConfig.START_EPOCH;
import static self.tekichan.demo.yfinance4j.YFinanceConfig.TIMEOUT_MILLIS;
import static self.tekichan.demo.yfinance4j.util.DateTimeUtil.getEndOfTodayEpoch;
import static self.tekichan.demo.yfinance4j.util.StringUtil.urlEncode;

/**
 * Controller class for HistoricalQuote
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public class HistoricalQuoteCtrl {
    static final String HISTORICAL_QUOTE_URL_PATTERN = "https://query1.finance.yahoo.com/v7/finance/download/%1$s?period1=%2$d&period2=%3$d&interval=%4$s&events=history&includeAdjustedClose=true";

    Optional<String> symbol;
    Optional<Long> startDateEpoch;
    Optional<Long> endDateEpoch;
    Optional<String> recordInterval;
    Optional<Integer> timeoutMillis;
    Optional<Exception> lastException;

    /**
     * Constructor of HistoricalQuote Controller
     */
    public HistoricalQuoteCtrl() {
        this.symbol = Optional.empty();
        this.startDateEpoch = Optional.of(START_EPOCH);
        this.endDateEpoch = Optional.of(getEndOfTodayEpoch());
        this.recordInterval = Optional.of(toIntervalCode(Interval.DAILY));
        this.timeoutMillis = Optional.of(TIMEOUT_MILLIS);
        this.lastException = Optional.empty();
    }

    /**
     * Set quote code symbol
     * @param quoteCode quote code symbol
     * @return  the configured HistoricalQuoteCtrl
     */
    public HistoricalQuoteCtrl symbol(String quoteCode) {
        Objects.requireNonNull(quoteCode, "Quote symbol must exist for lookup.");
        this.symbol = Optional.of(quoteCode).map(String::toUpperCase);
        return this;
    }

    /**
     * Set starting date of historical data
     * @param startDate Starting date
     * @return  the configured HistoricalQuoteCtrl
     */
    public HistoricalQuoteCtrl startDate(LocalDate startDate) {
        this.startDateEpoch = Optional.ofNullable(startDate)
                .map(d -> d.toEpochSecond(LocalTime.of(0, 0, 0), ZoneOffset.UTC))
                .or(() -> Optional.of(START_EPOCH));
        return this;
    }

    /**
     * Set ending date of historical data
     * @param endDate   Ending date
     * @return  the configured HistoricalQuoteCtrl
     */
    public HistoricalQuoteCtrl endDate(LocalDate endDate) {
        this.endDateEpoch = Optional.ofNullable(endDate)
                .map(d -> d.toEpochSecond(LocalTime.of(23, 59, 59), ZoneOffset.UTC))
                .or(() -> Optional.of(getEndOfTodayEpoch()));
        return this;
    }

    /**
     * Set the interval of reporting of records
     * @param interval  Interval of reporting
     * @return  the configured HistoricalQuoteCtrl
     */
    public HistoricalQuoteCtrl interval(Interval interval) {
        this.recordInterval = Optional.of(toIntervalCode(Interval.DAILY));
        return this;
    }

    /**
     * Set read and connection timeout for HTTP connection
     * @param timeoutMillis timeout in milliseconds
     * @return  the configured HistoricalQuoteCtrl
     */
    public HistoricalQuoteCtrl timeout(Integer timeoutMillis) {
        this.timeoutMillis = Optional.ofNullable(timeoutMillis)
                .filter(t -> t > 0);
        return this;
    }

    /**
     * Get a list of historical data
     * @return  List of historical quote data
     */
    public List<HistoricalQuote> getHistoricalData() {
        try {
            return WebClientHelper.downloadCsvToList(getTargetUrl(), this.timeoutMillis.orElse(TIMEOUT_MILLIS), this::fromStreamToList);
        } catch (Exception ex) {
            this.lastException = Optional.of(ex);
            return Collections.emptyList();
        }
    }

    /**
     * Asynchronously get a list of historical data
     * @return  CompletableFuture of List of historical data
     */
    public CompletableFuture<List<HistoricalQuote>> getHistoricalDataAsync() {
        try {
            return WebClientHelper.downloadCsvToListAsync(getTargetUrl(), this.timeoutMillis.orElse(TIMEOUT_MILLIS), this::fromStreamToList);
        } catch (Exception ex) {
            this.lastException = Optional.of(ex);
            return CompletableFuture.failedFuture(ex);
        }
    }

    /**
     * Get Optional of Exception
     * @return  Optional of Exception when exception happens when getting the data or Optional.empty() if normal
     */
    public Optional<Exception> getLastException() {
        return this.lastException;
    }

    private String getTargetUrl() {
        return String.format(
                HISTORICAL_QUOTE_URL_PATTERN
                , urlEncode(this.symbol)
                , this.startDateEpoch.orElse(START_EPOCH)
                , this.endDateEpoch.orElse(getEndOfTodayEpoch())
                , this.recordInterval.orElse(toIntervalCode(Interval.DAILY))
        );
    }

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

    private String toIntervalCode(Interval interval) {
        return switch(interval) {
            case WEEKLY -> "1wk";
            case MONTHLY -> "1mo";
            default -> "1d";    // Default Daily
        };
    }
}
