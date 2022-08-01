package self.tekichan.demo.yfinance4j.util;

import org.junit.jupiter.api.Test;
import self.tekichan.demo.yfinance4j.model.Interval;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static self.tekichan.demo.yfinance4j.YFinanceConfig.*;

/**
 * Unit Test for WebClientHelper
 * @author Teki Chan
 * @since 26 Jul 2022
 */
public class WebClientHelperTest {
    static final String HISTORICAL_QUOTE_URL_PATTERN = "https://query1.finance.yahoo.com/v7/finance/download/%1$s?period1=%2$d&period2=%3$d&interval=%4$s&events=history&includeAdjustedClose=true";

    @Test
    public void testDownloadCsvToList() throws Exception {
        var quoteCode = URLEncoder.encode("^FTSE", StandardCharsets.UTF_8.toString());
        var endEpoch = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochSecond(ZoneOffset.UTC);
        var internal = toIntervalCode(Interval.MONTHLY);
        var targetUrl = String.format(HISTORICAL_QUOTE_URL_PATTERN, quoteCode, START_EPOCH,  endEpoch, internal);
        List<String> result = WebClientHelper.downloadCsvToList(targetUrl, TIMEOUT_MILLIS, this::fromStreamToJson);
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.get(0).length() > 0);
        assertTrue(result.get(result.size() - 1).length() > 0);
        // System.out.println(result.get(result.size() - 1));
    }

    @Test
    public void testDownloadCsvToListAsync() throws Exception {
        var quoteCode = URLEncoder.encode("^FTSE", StandardCharsets.UTF_8.toString());
        var endEpoch = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).toEpochSecond(ZoneOffset.UTC);
        var internal = toIntervalCode(Interval.MONTHLY);
        var targetUrl = String.format(HISTORICAL_QUOTE_URL_PATTERN, quoteCode, START_EPOCH,  endEpoch, internal);
        CompletableFuture<List<String>> resultFuture = WebClientHelper.downloadCsvToListAsync(targetUrl, TIMEOUT_MILLIS, this::fromStreamToJson);
        Thread.sleep(TIMEOUT_MILLIS / 2);
        List<String> result = resultFuture.get(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.get(0).length() > 0);
        assertTrue(result.get(result.size() - 1).length() > 0);
        // System.out.println(result.get(result.size() - 1));
    }

    private List<String> fromStreamToJson(Stream<String> lines) {
        return lines.skip(1)
                .map(line -> {
                    try {
                        String[] items = line.split(",");
                        return
"""
{
  "trade_date": "%1$s"
  , "open": %2$s
  , "high": %3$s
  , "low": %4$s
  , "close": %5$s
  , "adj_close": %6$s
  , "volume": %7$s
}
"""
        .formatted(items);
                    } catch(Exception ex) {
                        return null;
                    }
                })
                .filter(item -> item != null)
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
