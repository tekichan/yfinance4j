package self.tekichan.demo.yfinance4j;

import org.junit.jupiter.api.Test;
import self.tekichan.demo.yfinance4j.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.*;
import static self.tekichan.demo.yfinance4j.YFinanceConfig.TIMEOUT_MILLIS;

/**
 * Unit Test class for YFinance4J
 * @author Teki Chan
 * @since 26 Jul 2022
 */
public class YFinance4JTest {
    static final List<String> INDEX_LIST = List.of( "^FTSE", "^NYA", "^IXIC", "^HSI", "^NSEI" );
    static final List<String> STOCK_LIST = List.of( "RMV.L", "GOOG", "0005.HK", "IOC.NS", "6753.T" );

    @Test
    public void testHistoricalQuoteCtrlBasic() {
        INDEX_LIST.forEach(
                testSymbol -> {
                    List<HistoricalQuote> result = YFinance4J.historicalQuoteCtrl()
                            .symbol(testSymbol)
                            .getHistoricalData();
                    assertNotNull(result);
                    assertTrue(result.size() > 0);
                    validateHistoricalQuote(result.get(0), testSymbol);
                    validateHistoricalQuote(result.get(result.size() - 1), testSymbol);
                }
        );
    }

    @Test
    public void testHistoricalQuoteCtrlAsyncBasic() {
        INDEX_LIST.forEach(
                testSymbol -> {
                    CompletableFuture<List<HistoricalQuote>> resultFuture = YFinance4J.historicalQuoteCtrl()
                            .symbol(testSymbol)
                            .getHistoricalDataAsync();
                    try {
                        Thread.sleep(RandomGenerator.getDefault().nextInt(2000, TIMEOUT_MILLIS/2));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    List<HistoricalQuote> result = null;
                    try {
                        result = resultFuture.get();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    assertNotNull(result);
                    assertTrue(result.size() > 0);
                    validateHistoricalQuote(result.get(0), testSymbol);
                    validateHistoricalQuote(result.get(result.size() - 1), testSymbol);
                }
        );
    }

    public void validateHistoricalQuote(HistoricalQuote quote, String expectedSymbol) {
        assertNotNull(quote);
        assertEquals(expectedSymbol, quote.symbol());
        assertTrue(quote.tradeDate().isBefore(LocalDate.now()) || quote.tradeDate().equals(LocalDate.now()));
        assertNotNull(quote.openPrice());
        assertNotNull(quote.openPrice());
        assertNotNull(quote.closePrice());
        assertNotNull(quote.closePrice());
        assertNotNull(quote.adjustPrice());
        assertTrue(quote.volume() >= 0L);
    }

    @Test
    public void testIndexComponentCtrlBasic() {
        INDEX_LIST.forEach(
                testSymbol -> {
                    IndexComponentInfo info = YFinance4J.indexComponentCtrl()
                            .symbol(testSymbol)
                            .getIndexComponentInfo();
                    assertNotNull(info);
                    assertEquals(testSymbol, info.symbol());
                    assertNotNull(info.componentList());
                    assertTrue(info.componentList().size() > 0);
                    info.componentList().forEach(this::validateIndexComponent);
                }
        );
    }

    @Test
    public void testIndexComponentCtrlBasicAsync() throws InterruptedException, ExecutionException {
        INDEX_LIST.forEach(
                testSymbol -> {
                    CompletableFuture<IndexComponentInfo> infoFuture = YFinance4J.indexComponentCtrl()
                            .symbol(testSymbol)
                            .getIndexComponentInfoAsync();
                    try {
                        Thread.sleep(RandomGenerator.getDefault().nextInt(2000, TIMEOUT_MILLIS/2));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    IndexComponentInfo info = null;
                    try {
                        info = infoFuture.get();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    assertNotNull(info);
                    assertEquals(testSymbol, info.symbol());
                    assertNotNull(info.componentList());
                    assertTrue(info.componentList().size() > 0);
                    info.componentList().forEach(this::validateIndexComponent);
                }
        );
    }

    private void validateIndexComponent(IndexComponent comp) {
        assertNotNull(comp.symbol());
        assertNotNull(comp.companyName());
        assertNotNull(comp.lastPrice());
        assertNotNull(comp.change());
        assertNotNull(comp.percentChange());
        assertTrue(comp.volume() > 0L);
    }

    @Test
    public void testSummaryQuoteCtrlBasic() {
        STOCK_LIST.forEach(
                testSymbol -> {
                    SummaryQuote quote = YFinance4J.summaryQuoteCtrl()
                            .symbol(testSymbol)
                            .getSummaryQuote();
                    validateSummaryQuote(quote, testSymbol);
                }
        );
    }

    @Test
    public void testSummaryQuoteCtrlBasicAsync() throws InterruptedException, ExecutionException {
        STOCK_LIST.forEach(
                testSymbol -> {
                    CompletableFuture<SummaryQuote> quoteFuture = YFinance4J.summaryQuoteCtrl()
                            .symbol(testSymbol)
                            .getSummaryQuoteAsync();
                    try {
                        Thread.sleep(RandomGenerator.getDefault().nextInt(2000, TIMEOUT_MILLIS/2));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    SummaryQuote quote = null;
                    try {
                        quote = quoteFuture.get();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    validateSummaryQuote(quote, testSymbol);
                }
        );
    }

    private void validateSummaryQuote(SummaryQuote quote, String expectedSymbol) {
        assertNotNull(quote);
        assertEquals(expectedSymbol, quote.symbol());
        assertNotNull(quote.downloadDateTime());
        assertNotNull(quote.stockExchange());
        assertNotNull(quote.currencyCode());
        assertNotNull(quote.previousPrice());
        assertNotNull(quote.openPrice());
        assertNotNull(quote.bidPrice());
        assertNotNull(quote.bidCount());
        assertNotNull(quote.askPrice());
        assertNotNull(quote.askCount());
        assertNotNull(quote.dayLow());
        assertNotNull(quote.dayHigh());
        assertTrue(quote.dayLow().compareTo(quote.dayHigh()) <= 0);
        assertNotNull(quote.fiftyTwoWeekLow());
        assertNotNull(quote.fiftyTwoWeekHigh());
        assertTrue(quote.fiftyTwoWeekLow().compareTo(quote.fiftyTwoWeekHigh()) <= 0);
        assertNotNull(quote.volume());
        assertNotNull(quote.averageVolume());
        assertNotNull(quote.marketCap());
        assertNotNull(quote.beta());
        assertNotNull(quote.peRatio());
        assertNotNull(quote.eps());
        assertNotNull(quote.earningsDate());
        assertNotNull(quote.forwardDividend());
        assertNotNull(quote.forwardYieldPercent());
        assertNotNull(quote.exDividendDate());
        assertNotNull(quote.oneYearEst());
    }

    @Test
    public void testKeyStatisticsCtrlBasic() {
        STOCK_LIST.forEach(
                testSymbol -> {
                    KeyStatistics stat = YFinance4J.keyStatisticsCtrl()
                            .symbol(testSymbol)
                            .getKeyStatistics();
                    validateKeyStatistics(stat, testSymbol);
                }
        );
    }

    @Test
    public void testKeyStatisticsCtrlBasicAsync() throws InterruptedException, ExecutionException {
        STOCK_LIST.forEach(
                testSymbol -> {
                    CompletableFuture<KeyStatistics> statFuture = YFinance4J.keyStatisticsCtrl()
                            .symbol(testSymbol)
                            .getKeyStatisticsAsync();
                    try {
                        Thread.sleep(RandomGenerator.getDefault().nextInt(2000, TIMEOUT_MILLIS/2));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    KeyStatistics stat = null;
                    try {
                        stat = statFuture.get();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    validateKeyStatistics(stat, testSymbol);
                }
        );
    }

    private void validateKeyStatistics(KeyStatistics stat, String expectedSymbol) {
        assertNotNull(stat);
        assertEquals(expectedSymbol, stat.symbol());
        assertNotNull(stat.downloadDateTime());
        assertNotNull(stat.companyName());
        assertNotNull(stat.stockExchange());
        assertNotNull(stat.currencyCode());
        assertNotNull(stat.marketCap());
        assertNotNull(stat.enterpriseValue());
        assertNotNull(stat.trailingPe());
        assertNotNull(stat.forwardPe());
        assertNotNull(stat.pegRatio());
        assertNotNull(stat.priceSalesRatio());
        assertNotNull(stat.priceBookRatio());
        assertNotNull(stat.enterpriseValueRevenueRatio());
        assertNotNull(stat.enterpriseValueEbitaRatio());
        assertNotNull(stat.fiscalYearEnds());
        assertNotNull(stat.mostRecentQuarter());
        assertNotNull(stat.profitMarginPercent());
        assertNotNull(stat.operatingMarginPercent());
        assertNotNull(stat.returnOnAssetsPercent());
        assertNotNull(stat.returnOnEquityPercent());
        assertNotNull(stat.revenue());
        assertNotNull(stat.revenuePerShare());
        assertNotNull(stat.quarterlyRevenueGrowthPercent());
        assertNotNull(stat.grossProfit());
        assertNotNull(stat.ebitda());
        assertNotNull(stat.netIncomeAviToCommon());
        assertNotNull(stat.dilutedEps());
        assertNotNull(stat.quarterlyEarningsGrowthPercent());
        assertNotNull(stat.totalCash());
        assertNotNull(stat.totalCashPerShare());
        assertNotNull(stat.totalDebt());
        assertNotNull(stat.totalDebtEquityRatio());
        assertNotNull(stat.currentRatio());
        assertNotNull(stat.bookValuePerShare());
        assertNotNull(stat.operatingCashFlow());
        assertNotNull(stat.leveredFreeCashFlow());
        assertNotNull(stat.beta());
        assertNotNull(stat.fiftyTwoWeekChangePercent());
        assertNotNull(stat.snp50052WeekChangePercent());
        assertNotNull(stat.fiftyTwoWeekHigh());
        assertNotNull(stat.fiftyTwoWeekLow());
        assertNotNull(stat.fiftyDayMovingAverage());
        assertNotNull(stat.twoHundredDayMovingAverage());
        assertNotNull(stat.avgVol3Month());
        assertNotNull(stat.avgVol10Day());
        assertNotNull(stat.sharesOutstanding());
        assertNotNull(stat.impliedSharesOutstanding());
        assertNotNull(stat.sharesFloat());
        assertNotNull(stat.heldByInsidersPercent());
        assertNotNull(stat.heldByInstitutions());
        assertNotNull(stat.sharesShort());
        assertNotNull(stat.shortRatio());
        assertNotNull(stat.shortPercentOfFloat());
        assertNotNull(stat.sharesOutstandingShortPercent());
        assertNotNull(stat.sharesShortPriorMonth());
        assertNotNull(stat.forwardAnnualDividendRate());
        assertNotNull(stat.forwardAnnualDividendYieldPercent());
        assertNotNull(stat.trailingAnnualDividendRate());
        assertNotNull(stat.trailingAnnualDividendYieldPercent());
        assertNotNull(stat.fiveYearAverageDividendYieldPercent());
        assertNotNull(stat.payoutRatioPercent());
        assertNotNull(stat.dividendDate());
        assertNotNull(stat.exDividendDate());
        assertNotNull(stat.lastSplitFactor());
        assertNotNull(stat.lastSplitDate());
    }
}
