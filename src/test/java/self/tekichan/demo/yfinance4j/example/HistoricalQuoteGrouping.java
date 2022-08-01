package self.tekichan.demo.yfinance4j.example;

import self.tekichan.demo.yfinance4j.YFinance4J;

import static java.util.stream.Collectors.*;

public class HistoricalQuoteGrouping {
    public static String usage() {
        return """
Usage - java %1$s symbol
symbol: Stock code symbol. e.g: TSCO.L
""".formatted(HistoricalQuoteGrouping.class.getName());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(usage());
            System.exit(-1);
        }

        String symbol = args[0];
        var historicalQuoteList = YFinance4J.historicalQuoteCtrl()
                .symbol(symbol)
                .getHistoricalData();
        var result = historicalQuoteList.stream()
                .collect(
                    groupingBy(quote -> quote.tradeDate().getYear(),
                            filtering(quote -> quote.openPrice().compareTo(quote.closePrice()) < 0,
                                    counting()
                            )
                    )
                );
        System.out.println("For %1$s, bullish days count per year:".formatted(symbol));
        System.out.println("-*-".repeat(20));
        System.out.println(result);
    }
}
