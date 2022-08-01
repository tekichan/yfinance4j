package self.tekichan.demo.yfinance4j.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Utility class for Date and Time
 * @author Teki Chan
 * @since 27 Jul 2022
 */
public final class DateTimeUtil {
    private DateTimeUtil() {}

    /**
     * Get Epoch of End of Today
     * @return  Epoch of End of Today
     */
    public static Long getEndOfTodayEpoch() {
        return getTodayEpoch(23, 59, 59);
    }

    /**
     * Get Epoch of Today with time specified
     * @param hour  Hour
     * @param min   Minute
     * @param second    Second
     * @return  Epoch of Today
     */
    public static Long getTodayEpoch(int hour, int min, int second) {
        return LocalDateTime.now().withHour(hour).withMinute(min).withSecond(second).toEpochSecond(ZoneOffset.UTC);
    }
}
