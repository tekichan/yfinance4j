package self.tekichan.demo.yfinance4j.example;

import self.tekichan.demo.yfinance4j.YFinance4J;

public class IndexComponentPe {
    public static String usage() {
        return """
Usage - java %1$s symbol
symbol: Index code symbol without caret. e.g: FTSE
""".formatted(IndexComponentPe.class.getName());
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(usage());
            System.exit(-1);
        }

        String symbol = "^" + args[0];
        var componentList = YFinance4J.indexComponentCtrl()
                .symbol(symbol)
                .getIndexComponentInfo()
                .componentList();
        System.out.println("Index %1$s Component Stock P/E Ratio:".formatted(symbol));
        componentList.stream()
                .map(comp -> YFinance4J.summaryQuoteCtrl().symbol(comp.symbol()).getSummaryQuote())
                .forEach(quote -> System.out.printf("%1$s: %2$f%n", quote.symbol(), quote.peRatio()));
    }
}
