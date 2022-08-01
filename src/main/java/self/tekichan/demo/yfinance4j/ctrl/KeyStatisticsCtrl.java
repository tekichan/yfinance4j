package self.tekichan.demo.yfinance4j.ctrl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import self.tekichan.demo.yfinance4j.model.KeyStatistics;
import self.tekichan.demo.yfinance4j.util.StringUtil;
import self.tekichan.demo.yfinance4j.util.WebClientHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.HTTP_OK;
import static self.tekichan.demo.yfinance4j.YFinanceConfig.TIMEOUT_MILLIS;

/**
 * Controller class for KeyStatistics
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public class KeyStatisticsCtrl {
    static final String KEY_STAT_URL_PATTERN = "https://finance.yahoo.com/quote/%1$s/key-statistics?p=%1$s";
    static final String STAT_CURRENCY_REGEX = "(\\w+)\\s+.+Currency\\s+in\\s+(\\w+)";
    static final Pattern STAT_CURRENCY_PATTERN = Pattern.compile(STAT_CURRENCY_REGEX, Pattern.CASE_INSENSITIVE);
    static final String COMP_NAME_REGEX = "\\w+\\s-\\s(.+)";
    static final Pattern COMP_NAME_PATTERN = Pattern.compile(COMP_NAME_REGEX, Pattern.CASE_INSENSITIVE);
    static final String COMP_NAME_O2_REGEX = "(.+)\\s\\(.+\\)";
    static final Pattern COMP_NAME_O2_PATTERN = Pattern.compile(COMP_NAME_O2_REGEX, Pattern.CASE_INSENSITIVE);  // company name pattern (option 2)
    static final String CSS_SELECT_COMP_NAME_PATTERN = "h1[class~=D]";
    static final String CSS_SELECT_STAT_CURR_PATTERN = "div[class~=C] > span";
    static final String CSS_SELECT_VALU_MEAS_PATTERN = "tr.fi-row > td:nth-child(2)";   // Valuation Measures section
    static final String CSS_SELECT_FINA_HIGH_PATTERN = "div[class~=Fl]:nth-child(3) > div:nth-child(2) > div > div:nth-child(1) > div:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr > td:nth-child(2)"; // Financial Highlights section
    static final String CSS_SELECT_FINA_HIGH_O2_PATTERN = "div[class~=Mb]:nth-child(3) > div > div:nth-child(1) > div:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr > td:nth-child(2)"; // Financial Highlights section (option 2)
    static final String CSS_SELECT_TRAD_INFO_PATTERN = "div[class~=Pstart] > div > div:nth-child(1) > div:nth-child(1) > table:nth-child(2) > tbody:nth-child(1) > tr > td:nth-child(2)";   // Trading Information section

    Optional<String> symbol;
    Optional<Integer> timeoutMillis;
    Optional<Exception> lastException;

    /**
     * Constructor of Key Statistics Controller
     */
    public KeyStatisticsCtrl() {
        this.symbol = Optional.empty();
        this.timeoutMillis = Optional.of(TIMEOUT_MILLIS);
        this.lastException = Optional.empty();
    }

    /**
     * Set quote code symbol of stock
     * @param quoteCode quote code symbol
     * @return  the configured controller
     */
    public KeyStatisticsCtrl symbol(String quoteCode) {
        Objects.requireNonNull(quoteCode, "Quote symbol must exist for lookup.");
        this.symbol = Optional.of(quoteCode).map(String::toUpperCase);
        return this;
    }

    /**
     * Set read and connection timeout for HTTP connection
     * @param timeoutMillis timeout in milliseconds
     * @return  the configured controller
     */
    public KeyStatisticsCtrl timeout(Integer timeoutMillis) {
        this.timeoutMillis = Optional.ofNullable(timeoutMillis)
                .filter(t -> t > 0);
        return this;
    }

    /**
     * Get Optional of Exception
     * @return  Optional of Exception when exception happens when getting the data or Optional.empty() if normal
     */
    public Optional<Exception> getLastException() {
        return this.lastException;
    }

    /**
     * Get Key Statistics of given stock
     * @return  Key Statistics
     */
    public KeyStatistics getKeyStatistics() {
        try {
            HttpResponse<String> response = WebClientHelper.getHttpResponse(getTargetUrl(), this.timeoutMillis.orElse(TIMEOUT_MILLIS));
            if (response.statusCode() == HTTP_OK) {
                return getKeyStatisticsFromBody(response.body());
            } else {
                this.lastException = Optional.of(new Exception("Unsuccessful Status Code: " + response.statusCode()));
                return null;
            }
        } catch (Exception ex) {
            this.lastException = Optional.of(ex);
            return null;
        }
    }

    /**
     * Asynchrously get Key Statistics of given stock
     * @return  CompletableFuture of Key Statistics
     */
    public CompletableFuture<KeyStatistics> getKeyStatisticsAsync() {
        try {
            return WebClientHelper.getHttpResponseAsync(getTargetUrl(), this.timeoutMillis.orElse(TIMEOUT_MILLIS))
                    .thenApply(HttpResponse::body)
                    .thenApply(this::getKeyStatisticsFromBody);
        } catch (Exception ex) {
            this.lastException = Optional.of(ex);
            return CompletableFuture.failedFuture(ex);
        }
    }

    private KeyStatistics getKeyStatisticsFromBody(String body) {
        Document htmlDoc = Jsoup.parse(body);

        var companyString = htmlDoc.select(CSS_SELECT_COMP_NAME_PATTERN).first().text();
        var matcherCompany = COMP_NAME_PATTERN.matcher(companyString);
        var companyName = this.symbol.get();
        if (matcherCompany.find()) {
            companyName = matcherCompany.group(1);
        } else {
            matcherCompany = COMP_NAME_O2_PATTERN.matcher(companyString);
            if (matcherCompany.find()) {
                companyName = matcherCompany.group(1);
            }
        }

        var htmlCurrency = htmlDoc.select(CSS_SELECT_STAT_CURR_PATTERN).get(2).text();   // the second element is about currency
        var matcherCurrency = STAT_CURRENCY_PATTERN.matcher(htmlCurrency);
        var stockExchange = "N/A";
        var currencyCode = "N/A";
        if (matcherCurrency.find()) {
            stockExchange = matcherCurrency.group(1);
            currencyCode = matcherCurrency.group(2);
        }

        // Valuation Measures
        var valuMeasElements = htmlDoc.select(CSS_SELECT_VALU_MEAS_PATTERN);
        var marketCapAndUnit = StringUtil.getBigDecimalAndUnit(valuMeasElements.get(0).text());
        var enterpriseValueAndUnit = StringUtil.getBigDecimalAndUnit(valuMeasElements.get(1).text());
        var trailingPe = StringUtil.getBigDecimal(valuMeasElements.get(2).text());
        var forwardPe = StringUtil.getBigDecimal(valuMeasElements.get(3).text());
        var pegRatio = StringUtil.getBigDecimal(valuMeasElements.get(4).text());
        var priceSalesRatio = StringUtil.getBigDecimal(valuMeasElements.get(5).text());
        var priceBookRatio = StringUtil.getBigDecimal(valuMeasElements.get(6).text());
        var enterpriseValueRevenueRatio = StringUtil.getBigDecimal(valuMeasElements.get(7).text());
        var enterpriseValueEbitaRatio = StringUtil.getBigDecimal(valuMeasElements.get(8).text());

        var finaHighElements = htmlDoc.select(CSS_SELECT_FINA_HIGH_PATTERN);
        if (finaHighElements.size() == 0) {
            finaHighElements = htmlDoc.select(CSS_SELECT_FINA_HIGH_O2_PATTERN);
        }
        // Fiscal Year
        var fiscalYearEnds = StringUtil.getLocalDate(finaHighElements.get(0).text());
        var mostRecentQuarter = StringUtil.getLocalDate(finaHighElements.get(1).text());
        // Profitability
        var profitMarginPercent = StringUtil.getBigDecimalPercent(finaHighElements.get(2).text());
        var operatingMarginPercent = StringUtil.getBigDecimalPercent(finaHighElements.get(3).text());
        // Management Effectiveness
        var returnOnAssetsPercent = StringUtil.getBigDecimalPercent(finaHighElements.get(4).text());
        var returnOnEquityPercent = StringUtil.getBigDecimalPercent(finaHighElements.get(5).text());
        // Income Statement
        var revenueAndUnit = StringUtil.getBigDecimalAndUnit(finaHighElements.get(6).text());
        var revenuePerShare = StringUtil.getBigDecimal(finaHighElements.get(7).text());
        var quarterlyRevenueGrowthPercent = StringUtil.getBigDecimalPercent(finaHighElements.get(8).text());
        var grossProfitAndUnit = StringUtil.getBigDecimalAndUnit(finaHighElements.get(9).text());
        var ebitdaAndUnit = StringUtil.getBigDecimalAndUnit(finaHighElements.get(10).text());
        var netIncomeAviToCommonAndUnit = StringUtil.getBigDecimalAndUnit(finaHighElements.get(11).text());
        var dilutedEps = StringUtil.getBigDecimal(finaHighElements.get(12).text());
        var quarterlyEarningsGrowthPercent = StringUtil.getBigDecimalPercent(finaHighElements.get(13).text());
        // Balance Sheet
        var totalCashAndUnit = StringUtil.getBigDecimalAndUnit(finaHighElements.get(14).text());
        var totalCashPerShare = StringUtil.getBigDecimal(finaHighElements.get(15).text());
        var totalDebtAndUnit = StringUtil.getBigDecimalAndUnit(finaHighElements.get(16).text());
        var totalDebtEquityRatio = StringUtil.getBigDecimal(finaHighElements.get(17).text());
        var currentRatio = StringUtil.getBigDecimal(finaHighElements.get(18).text());
        var bookValuePerShare = StringUtil.getBigDecimal(finaHighElements.get(19).text());
        // Cash Flow Statement
        var operatingCashFlowAndUnit = StringUtil.getBigDecimalAndUnit(finaHighElements.get(20).text());
        var leveredFreeCashFlow = StringUtil.getBigDecimalAndUnit(finaHighElements.get(21).text());

        var tradInfoElements = htmlDoc.select(CSS_SELECT_TRAD_INFO_PATTERN);
        // Stock Price History
        var beta = StringUtil.getBigDecimal(tradInfoElements.get(0).text());
        var fiftyTwoWeekChangePercent = StringUtil.getBigDecimalPercent(tradInfoElements.get(1).text());
        var snp50052WeekChangePercent = StringUtil.getBigDecimalPercent(tradInfoElements.get(2).text());
        var fiftyTwoWeekHigh = StringUtil.getBigDecimal(tradInfoElements.get(3).text());
        var fiftyTwoWeekLow = StringUtil.getBigDecimal(tradInfoElements.get(4).text());
        var fiftyDayMovingAverage = StringUtil.getBigDecimal(tradInfoElements.get(5).text());
        var twoHundredDayMovingAverage = StringUtil.getBigDecimal(tradInfoElements.get(6).text());
        var avgVol3MonthAndUnit = StringUtil.getBigDecimalAndUnit(tradInfoElements.get(7).text());
        var avgVol10DayAndUnit = StringUtil.getBigDecimalAndUnit(tradInfoElements.get(8).text());
        var sharesOutstandingAndUnit = StringUtil.getBigDecimalAndUnit(tradInfoElements.get(9).text());
        var impliedSharesOutstandingAndUnit = StringUtil.getBigDecimalAndUnit(tradInfoElements.get(10).text());
        var sharesFloatAndUnit = StringUtil.getBigDecimalAndUnit(tradInfoElements.get(11).text());
        var heldByInsidersPercent = StringUtil.getBigDecimalPercent(tradInfoElements.get(12).text());
        var heldByInstitutions = StringUtil.getBigDecimal(tradInfoElements.get(13).text());
        var sharesShortAndUnit = StringUtil.getBigDecimalAndUnit(tradInfoElements.get(14).text());
        var shortRatio = StringUtil.getBigDecimal(tradInfoElements.get(15).text());
        var shortPercentOfFloat = StringUtil.getBigDecimal(tradInfoElements.get(16).text());
        var sharesOutstandingShortPercent = StringUtil.getBigDecimalPercent(tradInfoElements.get(17).text());
        var sharesShortPriorMonth = StringUtil.getBigDecimalAndUnit(tradInfoElements.get(18).text());
        var forwardAnnualDividendRate = StringUtil.getBigDecimal(tradInfoElements.get(19).text());
        var forwardAnnualDividendYieldPercent = StringUtil.getBigDecimalPercent(tradInfoElements.get(20).text());
        var trailingAnnualDividendRate = StringUtil.getBigDecimal(tradInfoElements.get(21).text());
        var trailingAnnualDividendYieldPercent = StringUtil.getBigDecimalPercent(tradInfoElements.get(22).text());
        var fiveYearAverageDividendYieldPercent = StringUtil.getBigDecimalPercent(tradInfoElements.get(23).text());
        var payoutRatioPercent = StringUtil.getBigDecimalPercent(tradInfoElements.get(24).text());
        var dividendDate = StringUtil.getLocalDate(tradInfoElements.get(25).text());
        var exDividendDate = StringUtil.getLocalDate(tradInfoElements.get(26).text());
        var lastSplitFactor = StringUtil.getFactorRatio(tradInfoElements.get(27).text());
        var lastSplitDate = StringUtil.getLocalDate(tradInfoElements.get(28).text());

        return new KeyStatistics(
                this.symbol.get()
                , LocalDateTime.now()
                , companyName
                , stockExchange
                , currencyCode
                , marketCapAndUnit
                , enterpriseValueAndUnit
                , trailingPe
                , forwardPe
                , pegRatio
                , priceSalesRatio
                , priceBookRatio
                , enterpriseValueRevenueRatio
                , enterpriseValueEbitaRatio
                , fiscalYearEnds
                , mostRecentQuarter
                , profitMarginPercent
                , operatingMarginPercent
                , returnOnAssetsPercent
                , returnOnEquityPercent
                , revenueAndUnit
                , revenuePerShare
                , quarterlyRevenueGrowthPercent
                , grossProfitAndUnit
                , ebitdaAndUnit
                , netIncomeAviToCommonAndUnit
                , dilutedEps
                , quarterlyEarningsGrowthPercent
                , totalCashAndUnit
                , totalCashPerShare
                , totalDebtAndUnit
                , totalDebtEquityRatio
                , currentRatio
                , bookValuePerShare
                , operatingCashFlowAndUnit
                , leveredFreeCashFlow
                , beta
                , fiftyTwoWeekChangePercent
                , snp50052WeekChangePercent
                , fiftyTwoWeekHigh
                , fiftyTwoWeekLow
                , fiftyDayMovingAverage
                , twoHundredDayMovingAverage
                , avgVol3MonthAndUnit
                , avgVol10DayAndUnit
                , sharesOutstandingAndUnit
                , impliedSharesOutstandingAndUnit
                , sharesFloatAndUnit
                , heldByInsidersPercent
                , heldByInstitutions
                , sharesShortAndUnit
                , shortRatio
                , shortPercentOfFloat
                , sharesOutstandingShortPercent
                , sharesShortPriorMonth
                , forwardAnnualDividendRate
                , forwardAnnualDividendYieldPercent
                , trailingAnnualDividendRate
                , trailingAnnualDividendYieldPercent
                , fiveYearAverageDividendYieldPercent
                , payoutRatioPercent
                , dividendDate
                , exDividendDate
                , lastSplitFactor
                , lastSplitDate
        );
    }

    private String getQuoteCode() {
        try {
            return URLEncoder.encode(this.symbol.get(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return this.symbol.get();
        }
    }

    private String getTargetUrl() {
        return String.format(
                KEY_STAT_URL_PATTERN
                , getQuoteCode()
        );
    }    
}
