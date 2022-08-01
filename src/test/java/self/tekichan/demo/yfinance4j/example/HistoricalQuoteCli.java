package self.tekichan.demo.yfinance4j.example;

import self.tekichan.demo.yfinance4j.YFinance4J;
import self.tekichan.demo.yfinance4j.YFinanceConfig;
import self.tekichan.demo.yfinance4j.model.HistoricalQuote;
import self.tekichan.demo.yfinance4j.model.Interval;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoricalQuoteCli {
    static final String DATE_FORMAT = "yyyy-MM-dd";
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static String usage() {
        return """
Usage - java %1$s symbol [-t timeout] [-s startdate] [-e enddate] [-i interval]
symbol: Stock code symbol. e.g: TSCO.L
timeout: Timeout in second. e.g: 1
startdate: Starting date in yyyy-MM-dd.
enddate: Ending date in yyyy-MM-dd.
interval: Reportinng interval. d: Daily, w: Weekly, m: Monthly
""".formatted(HistoricalQuoteCli.class.getName());
    }

    public static String toJson(HistoricalQuote quote) {
        return """
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
        );
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(usage());
            System.exit(-1);
        }

        var symbol = args[0];
        var timeout = YFinanceConfig.TIMEOUT_MILLIS;
        var startDate = LocalDate.of(1985, 12, 28);
        var endDate = LocalDate.now();
        var interval = Interval.DAILY;

        int i=1;
        while(i < args.length) {
            if ("-t".equals(args[i])) {
                i++;
                try {
                    timeout = Integer.parseInt(args[i]);
                } catch (Exception ex) {
                    System.out.println(usage());
                    System.exit(-1);
                }
            }
            if ("-s".equals(args[i])) {
                i++;
                try {
                    startDate = LocalDate.parse(args[i], DATE_FORMATTER);
                } catch (Exception ex) {
                    System.out.println(usage());
                    System.exit(-1);
                }
            }
            if ("-e".equals(args[i])) {
                i++;
                try {
                    endDate = LocalDate.parse(args[i], DATE_FORMATTER);
                } catch (Exception ex) {
                    System.out.println(usage());
                    System.exit(-1);
                }
            }
            if ("-i".equals(args[i])) {
                i++;
                try {
                    interval = switch(args[i].charAt(0)) {
                        case 'w' -> Interval.WEEKLY;
                        case 'm' -> Interval.MONTHLY;
                        default -> Interval.DAILY;
                    };
                } catch (Exception ex) {
                    System.out.println(usage());
                    System.exit(-1);
                }
            }
            i++;
        }

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
                                        .map(quote ->
"""
{
  "trade_date": "%1$s"
  , "open": %2$s
  , "high": %3$s
  , "low": %4$s
  , "close": %5$s
  , "adj_close": %6$s
  , "volume": %7$s
}""".formatted(
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
    }
}
