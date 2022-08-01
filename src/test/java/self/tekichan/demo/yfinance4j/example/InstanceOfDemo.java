package self.tekichan.demo.yfinance4j.example;

import self.tekichan.demo.yfinance4j.YFinance4J;
import self.tekichan.demo.yfinance4j.model.IStockQuote;
import self.tekichan.demo.yfinance4j.model.KeyStatistics;
import self.tekichan.demo.yfinance4j.model.SummaryQuote;

import java.util.List;

public class InstanceOfDemo {
    public static String usage() {
        return """
Usage - java %1$s symbol
symbol: Stock code symbol. e.g: TSCO.L
""".formatted(InstanceOfDemo.class.getName());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(usage());
            System.exit(-1);
        }

        String symbol = args[0];
        IStockQuote info1 = YFinance4J.summaryQuoteCtrl().symbol(symbol).getSummaryQuote();
        IStockQuote info2 = YFinance4J.keyStatisticsCtrl().symbol(symbol).getKeyStatistics();
        var infoList = List.of(info1, info2);
        infoList.forEach(
                info -> {
                    if (info instanceof SummaryQuote sq) {
                        System.out.println(sq.symbol());
                        System.out.println("Day Range: %1$f - %2$f".formatted(sq.dayLow(), sq.dayHigh()));
                    } else if (info instanceof KeyStatistics ks) {
                        System.out.println(ks.companyName());
                        System.out.println("50-day Moving Average: %1$f".formatted(ks.fiftyDayMovingAverage()));
                    }
                }
        );
    }
}
