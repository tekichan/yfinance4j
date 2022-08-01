package self.tekichan.demo.yfinance4j.model;

/**
 * A simple interface representing Stock Quote
 * <p>
 *     An example of using sealed interface.
 * </p>
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public sealed interface IStockQuote permits SummaryQuote, KeyStatistics {
    /**
     * Get description of the stock
     * @return  description
     */
    String getDescription();
}
