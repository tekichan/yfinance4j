package self.tekichan.demo.yfinance4j.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Historical Quote of Stock or Index
 * @author Teki Chan
 * @since 1 Jul 2022
 * @param symbol    Quote symbol
 * @param tradeDate     Trading date
 * @param openPrice     Open price
 * @param highPrice     Highest price on the day
 * @param lowPrice      Lowest price on the day
 * @param closePrice    Close price
 * @param adjustPrice   Adjusted close price
 * @param volume        Volume
 */
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
    private static String DATE_FORMAT = "yyyy-MM-dd";
    private static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    /**
     * Construct HistoricalQuote from String array
     * @param args  String array of HistoricalQuote parameters in order
     */
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
}
