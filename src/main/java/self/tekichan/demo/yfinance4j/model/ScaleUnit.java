package self.tekichan.demo.yfinance4j.model;

import java.math.BigDecimal;

/**
 * Enum for scale unit representation
 * <p>
 *     For some numeric values, especially for a very large number, the number
 *     is represented by a combined value of a base value and a scale unit.
 *     The scale unit is a character commonly used in English. e.g. K = 1000, M = 1 million.
 * </p>
 * @author  Teki Chan
 * @since   1 Jul 2022
 */
public enum ScaleUnit {
    /**
     * One - Default: 1
     */
    ONE,
    /**
     * Thousand - K = 1,000
     */
    THOUSAND,
    /**
     * Million - M = 1,000,000
     */
    MILLION,
    /**
     * Billion - B = 1,000,000,000
     */
    BILLION,
    /**
     * Trillion - T = 1,000,000,000,000
     */
    TRILLION,
    /**
     * Quadrillion - Q = 1,000,000,000,000,000
     */
    QUADRILLION;

    /**
     * Convert a character of scale representation to ScaleUnit instance
     * @param charVal   Scale representation character. e.g. K = 1,000.
     * @return  ScaleUnit instance; ScaleUnit.ONE by default.
     */
    public static ScaleUnit fromChar(Character charVal) {
        return switch (charVal) {
            case 'K' -> THOUSAND;
            case 'M' -> MILLION;
            case 'B' -> BILLION;
            case 'T' -> TRILLION;
            case 'Q' -> QUADRILLION;
            default -> ONE;
        };
    }

    /**
     * Convert a string of scale representation to ScaleUnit instance
     * @param textVal   Scale representation string. e.g. K = 1,000.
     * @return  ScaleUnit instance; ScaleUnit.ONE by default.
     */
    public static ScaleUnit fromString(String textVal) {
        return fromChar((textVal != null && textVal.length() >= 1)?textVal.charAt(0) : ' ');
    }

    /**
     * Return its Long value it represents
     * @return  Long value
     */
    public Long toLong() {
        return switch (this) {
            case THOUSAND -> 1_000L;
            case MILLION -> 1_000_000L;
            case BILLION -> 1_000_000_000L;
            case TRILLION -> 1_000_000_000_000L;
            case QUADRILLION -> 1_000_000_000_000_000L;
            default -> 1L;
        };
    }

    /**
     * Return its BigDecimal value it represents
     * @return  BigDecimal value
     */
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(this.toLong());
    }

    /**
     * Return the character representing its scale unit. e.g. K = 1000.
     * @return  the character representation
     */
    public Character toChar() {
        return switch (this) {
            case THOUSAND -> 'K';
            case MILLION -> 'M';
            case BILLION -> 'B';
            case TRILLION -> 'T';
            case QUADRILLION -> 'Q';
            default -> ' ';
        };
    }

    /**
     * Return the string representing its scale unit. e.g. K = 1000.
     * @return  the string representation
     */
    public String toString() {
        return this.toChar().toString();
    }
}
