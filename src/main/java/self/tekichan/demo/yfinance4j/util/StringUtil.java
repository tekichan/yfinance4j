package self.tekichan.demo.yfinance4j.util;

import self.tekichan.demo.yfinance4j.model.BigDecimalAndUnit;
import self.tekichan.demo.yfinance4j.model.FactorRatio;
import self.tekichan.demo.yfinance4j.model.ScaleUnit;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static self.tekichan.demo.yfinance4j.YFinanceConfig.*;

/**
 * Utility class to manipulate String objects
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public final class StringUtil {
    /**
     * Get URL encoded String from Optional of String
     * @param str   Optional of String
     * @return  URL encoded String
     */
    public static String urlEncode(Optional<String> str) {
        return urlEncode(str, "");
    }

    /**
     * Get URL encoded String from Optional of String
     * @param str   Optional of String
     * @param defaultString Default String if str is empty
     * @return  URL encoded String
     */
    public static String urlEncode(Optional<String> str, String defaultString) {
        return urlEncode(str, defaultString, StandardCharsets.UTF_8.toString());
    }

    /**
     * Get URL encoded String from Optional of String
     * @param str   Optional of String
     * @param defaultString Default String if str is empty
     * @param encoding  URL encoded String
     * @return
     */
    public static String urlEncode(Optional<String> str, String defaultString, String encoding) {
        Objects.requireNonNull(str, "Optional<String> parameter must not be null.");
        try {
            return URLEncoder.encode(str.orElse(defaultString), encoding);
        } catch (UnsupportedEncodingException e) {
            return str.orElse(defaultString);
        }
    }

    /**
     * Get BigDecimal value from input String value
     * <p>
     *     The String value can contain comma, e.g. 1,234.56. It does not matter in conversion.
     * </p>
     * @param textVal   String value
     * @return  BigDecimal value the string represents or 0 if invalid
     */
    public static BigDecimal getBigDecimal(String textVal) {
        try {
            return new BigDecimal(textVal.replace(",", ""));
        } catch(Exception ex) {
            return BigDecimal.valueOf(0L);
        }
    }

    /**
     * Get BigDecimal value from input String value representing percentage
     * <p>
     *     The String consists of numeric value and percent sign, e.g. 3.45% will get 3.45.
     * </p>
     * @param textVal   String with numeric value and percent sign
     * @return  BigDecimal value the string numeric part represents or 0 if invalid
     */
    public static BigDecimal getBigDecimalPercent(String textVal) {
        return getBigDecimalPercent(textVal, VALUE_WITH_PERCENT_PATTERN);
    }

    /**
     * Get BigDecimal value from input String value representing percentage
     * @param textVal   String with numeric value and percent sign
     * @param percentPattern    Pattern to represent the format of numeric value and percent sign
     * @return  BigDecimal value the string numeric part represents or 0 if invalid
     */
    public static BigDecimal getBigDecimalPercent(String textVal, Pattern percentPattern) {
        try {
            Matcher matcher = percentPattern.matcher(textVal);
            if (matcher.find()) {
                return new BigDecimal(matcher.group(1));
            }
            return getBigDecimal(textVal);
        } catch(Exception ex) {
            return BigDecimal.valueOf(0L);
        }
    }

    /**
     * Get Integer from input String value
     * @param textVal   String value
     * @return  Integer value the string represents or 0 if invalid
     */
    public static Integer getInteger(String textVal) {
        try {
            return Integer.valueOf(textVal.replace(",", ""));
        } catch(Exception ex) {
            return Integer.valueOf(0);
        }
    }

    /**
     * Get LocalDate from input String value
     * @see self.tekichan.demo.yfinance4j.YFinanceConfig
     * @param textVal   String value in {@value self.tekichan.demo.yfinance4j.YFinanceConfig#DATE_FORMAT}
     * @return  LocalDate the string represents or LocalDate.MIN if invalid
     */
    public static LocalDate getLocalDate(String textVal) {
        return getLocalDate(textVal, DATE_FORMATTER);
    }

    /**
     * Get LocalDate from input String value
     * @param textVal   String value in YFinanceConfig.DATE_FORMATTER
     * @param formatter Pattern of the date format, e.g. MMM dd, yyyy
     * @return  LocalDate the string represents or LocalDate.MIN if invalid
     */
    public static LocalDate getLocalDate(String textVal, DateTimeFormatter formatter) {
        try {
            return LocalDate.parse(textVal, formatter);
        } catch(Exception ex) {
            return LocalDate.MIN;
        }
    }

    /**
     * Get BigDecimalAndUnit from input String value
     * <p>
     *     The String value contains numeric value and scale unit, e.g. 1.35B = 1.35 billion
     * </p>
     * @param textVal   String value
     * @return  BigDecimalAndUnit the string represents or basic number conversion if invalid
     */
    public static BigDecimalAndUnit getBigDecimalAndUnit(String textVal) {
        return getBigDecimalAndUnit(textVal, VALUE_WITH_UNIT_PATTERN);
    }

    /**
     * Get BigDecimalAndUnit from input String value
     * @param textVal   String value
     * @param pattern   Pattern of numeric value and scale unit
     * @return  BigDecimalAndUnit the string represents or basic number conversion if invalid
     */
    public static BigDecimalAndUnit getBigDecimalAndUnit(String textVal, Pattern pattern) {
        Matcher matcher = pattern.matcher(textVal);
        if (matcher.find()) {
            return new BigDecimalAndUnit(
                    getBigDecimal(matcher.group(1))
                    , ScaleUnit.fromString(matcher.group(2))
            );
        } else {
            return new BigDecimalAndUnit(
                    getBigDecimal(textVal)
                    , ScaleUnit.ONE
            );
        }
    }

    /**
     * Get FactorRatio from input String value
     * <p>
     *     FactorRatio is in format of a:b, e.g. 1:3.
     * </p>
     * @param textVal   String value
     * @return  FactorRatio the string represents or 0:0 if invalid
     */
    public static FactorRatio getFactorRatio(String textVal) {
        return getFactorRatio(textVal, FACTOR_PATTERN);
    }

    /**
     * Get FactorRatio from input String value
     * @param textVal   String value
     * @param pattern   Pattern of the format with two numeric value and factor character
     * @return  FactorRatio the string represents or 0:0 if invalid
     */
    public static FactorRatio getFactorRatio(String textVal, Pattern pattern) {
        Matcher matcher = pattern.matcher(textVal);
        if (matcher.find()) {
            return new FactorRatio(
                    getBigDecimal(matcher.group(1))
                    , getBigDecimal(matcher.group(2))
            );
        } else {
            return new FactorRatio(
                    BigDecimal.valueOf(0L)
                    , BigDecimal.valueOf(0L)
            );
        }
    }
}
