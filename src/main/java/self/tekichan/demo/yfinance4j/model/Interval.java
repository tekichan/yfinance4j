package self.tekichan.demo.yfinance4j.model;

/**
 * Enum for Yahoo Finance Time Interval
 * <p>
 *     Representation of time interval. It is mainly used in retrieving time-sequential data, e.g. historical quotes.
 * </p>
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public enum Interval {
    /**
     * Daily
     */
    DAILY,
    /**
     * Weekly
     */
    WEEKLY,
    /**
     * Monthly
     */
    MONTHLY
}
