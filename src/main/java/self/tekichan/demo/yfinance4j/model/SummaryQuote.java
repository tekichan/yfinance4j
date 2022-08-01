package self.tekichan.demo.yfinance4j.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Summary inforamtion of stock quote
 * @author Teki Chan
 * @since 1 Jul 2022
 * @param symbol    Stock symbol
 * @param downloadDateTime  Date/Time of downloading the summary
 * @param companyName   Company Name
 * @param stockExchange Stock exchange (abbr)
 * @param currencyCode  Currency code used by the stock
 * @param previousPrice Previous price
 * @param openPrice     Open price
 * @param bidPrice  Bid price
 * @param bidCount  Number of bid
 * @param askPrice  Ask price
 * @param askCount  Number of ask
 * @param dayLow    Lowest price on the day
 * @param dayHigh   Highest price on the day
 * @param fiftyTwoWeekLow   Lowest price in the 52 weeks
 * @param fiftyTwoWeekHigh  Highest price in the 52 weeks
 * @param volume    Volume
 * @param averageVolume Average volume
 * @param marketCap Market Cap (intraday)
 * @param beta  Beta (5Y Monthly)
 * @param peRatio   P/E ratio (Trailing Twelve Months)
 * @param eps   Earning per share   (Trailing Twelve Months)
 * @param earningsDate  Earning date
 * @param forwardDividend   Forward dividend
 * @param forwardYieldPercent   Forward yield percentage
 * @param exDividendDate    Ex-Dividend date
 * @param oneYearEst    One year estimation
 */
public record SummaryQuote(
        String symbol
        , LocalDateTime downloadDateTime
        , String companyName
        , String stockExchange
        , String currencyCode
        , BigDecimal previousPrice
        , BigDecimal openPrice
        , BigDecimal bidPrice
        , Integer bidCount
        , BigDecimal askPrice
        , Integer askCount
        , BigDecimal dayLow
        , BigDecimal dayHigh
        , BigDecimal fiftyTwoWeekLow
        , BigDecimal fiftyTwoWeekHigh
        , Long volume
        , Long averageVolume
        , BigDecimalAndUnit marketCap
        , BigDecimal beta
        , BigDecimal peRatio
        , BigDecimal eps
        , LocalDate earningsDate
        , BigDecimal forwardDividend
        , BigDecimal forwardYieldPercent
        , LocalDate exDividendDate
        , BigDecimal oneYearEst
) implements IStockQuote {
    @Override
    public String getDescription() {
        return "%1$s - %2$s".formatted(this.symbol, this.companyName);
    }
}
