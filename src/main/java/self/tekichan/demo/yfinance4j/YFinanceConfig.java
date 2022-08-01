package self.tekichan.demo.yfinance4j;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Configuration/Constant values for YFinance4j
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public final class YFinanceConfig {
    private YFinanceConfig() {
        // restrict instantiation
    }

    /**
     * Timeout milliseconds for connection and read. The duration is {@value}.
     */
    public static final int TIMEOUT_MILLIS = 10000;
    /**
     * Default starting time epoch
     */
    public static final Long START_EPOCH = LocalDateTime.of(1985, Month.DECEMBER, 28, 0, 0).toEpochSecond(ZoneOffset.UTC);
    /**
     * Date format used in Yahoo! Finance
     */
    public static final String DATE_FORMAT = "MMM dd, yyyy";
    /**
     * Date Formatter using the format {@value YFinanceConfig#DATE_FORMAT}
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    /**
     * Regular expression for value with percent, e.g. 2.46%
     */
    public static final String VALUE_WITH_PERCENT_REGEX = "(\\d+\\.\\d*)%";
    /**
     * Pattern object representing the regular expression {@value YFinanceConfig#VALUE_WITH_PERCENT_REGEX}
     */
    public static final Pattern VALUE_WITH_PERCENT_PATTERN = Pattern.compile(VALUE_WITH_PERCENT_REGEX, Pattern.CASE_INSENSITIVE);
    /**
     * Regular expression for value with unit
     */
    public static final String VALUE_WITH_UNIT_REGEX = "(\\d+\\.*\\d*)([A-Z])";
    /**
     * Pattern object representing the regular expression {@value YFinanceConfig#VALUE_WITH_UNIT_REGEX}
     */
    public static final Pattern VALUE_WITH_UNIT_PATTERN = Pattern.compile(VALUE_WITH_UNIT_REGEX, Pattern.CASE_INSENSITIVE);
    /**
     * Regular expression for factor ratio, e.g. 1:3
     */
    public static final String FACTOR_REGEX = "(\\d+\\.?\\d*):(\\d+\\.?\\d*)";
    /**
     * Pattern object representing the regular expression {@value YFinanceConfig#FACTOR_REGEX}
     */
    public static final Pattern FACTOR_PATTERN = Pattern.compile(FACTOR_REGEX, Pattern.CASE_INSENSITIVE);
}
