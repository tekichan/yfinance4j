package self.tekichan.demo.yfinance4j;

import self.tekichan.demo.yfinance4j.ctrl.HistoricalQuoteCtrl;
import self.tekichan.demo.yfinance4j.ctrl.IndexComponentCtrl;
import self.tekichan.demo.yfinance4j.ctrl.KeyStatisticsCtrl;
import self.tekichan.demo.yfinance4j.ctrl.SummaryQuoteCtrl;

/**
 * Portal class for this library YFinance4J
 * <p>
 *     YFinance4J is a Java library to retrieve stock related quotes and information from Yahoo! Finance.
 *     The information includes historical quotes and key statistics.
 *     <br>
 *     The data source is from Yahoo! Finance US websites. Stocks on the US stock exchanges usually have more
 *     data available than stocks on other countries.
 *     <br>
 *     Because of Yahoo! Finance as the data source, Terms of Service should be referred to
 *     <a href="https://legal.yahoo.com/us/en/yahoo/terms/otos/index.html">Yahoo Terms of Service</a>.
 * </p>
 * <p>
 *     Actually this project aims at demonstrating key features of Java 17. Anyone is welcomed to fork this project
 *     and encouraged to enhance this with advanced and up-to-date Java features.
 * </p>
 * <p>
 *  <b>Acknowledgement</b>
 *  <br>
 *     This project is inspired by <a href="https://financequotes-api.com/">Quotes API for Yahoo Finance</a>
 *     and <a href="https://github.com/ranaroussi/yfinance">YFinance</a>. Thanks a lot for their efforts.
 * </p>
 * <p>
 *     <b>Disclaimer</b>
 *     <br>
 *     <i>This project is not associated with nor sponsored by Yahoo! Inc.
 *     Yahoo! Inc. is the exclusive owner of all trademark and other intellectual property rights in and
 *     to the YAHOO! and Y! trademarks (the "Trademarks"), including the stylized YAHOO! and Y! logos.
 *     Yahoo! Inc. owns trademark registrations for the Trademarks.</i>
 * </p>
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public final class YFinance4J {
    private YFinance4J() {
        // restrict instantiation
    }

    /**
     * Get Historical Quote Controller for building data requester
     * @return  Historical Quote Controller
     */
    public static HistoricalQuoteCtrl historicalQuoteCtrl() {
        return new HistoricalQuoteCtrl();
    }
    /**
     * Get Index Component Controller for building data requester
     * @return  Index Component Controller
     */
    public static IndexComponentCtrl indexComponentCtrl() {
        return new IndexComponentCtrl();
    }
    /**
     * Get Summary Quote Controller for building data requester
     * @return  Summary Quote Controller
     */
    public static SummaryQuoteCtrl summaryQuoteCtrl() { return new SummaryQuoteCtrl(); }

    /**
     * Get Key Statistics Controller for building data requester
     * @return  Key Statistics Controller
     */
    public static KeyStatisticsCtrl keyStatisticsCtrl() { return new KeyStatisticsCtrl(); }
}
