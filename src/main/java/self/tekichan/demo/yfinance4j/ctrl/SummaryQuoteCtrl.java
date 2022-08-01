package self.tekichan.demo.yfinance4j.ctrl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import self.tekichan.demo.yfinance4j.model.SummaryQuote;
import self.tekichan.demo.yfinance4j.util.StringUtil;
import self.tekichan.demo.yfinance4j.util.WebClientHelper;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
 * Controller class for SummaryQuote
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public class SummaryQuoteCtrl {
    static final String SUMM_QUOTE_URL_PATTERN = "https://finance.yahoo.com/quote/%1$s?p=%1$s";
    static final String SUMM_CURRENCY_REGEX = "(\\w+)\\s+.+Currency\\s+in\\s+(\\w+)";
    static final Pattern SUMM_CURRENCY_PATTERN = Pattern.compile(SUMM_CURRENCY_REGEX, Pattern.CASE_INSENSITIVE);
    static final String BID_ASK_REGEX = "([,\\d]+\\.?\\d*)\\s+x\\s+([,\\d]+)";
    static final Pattern BID_ASK_PATTERN = Pattern.compile(BID_ASK_REGEX, Pattern.CASE_INSENSITIVE);
    static final String PRICE_RANGE_REGEX = "([,\\d]+\\.?\\d*)\\s+-\\s+([,\\d]+\\.?\\d*)";
    static final Pattern PRICE_RANGE_PATTERN = Pattern.compile(PRICE_RANGE_REGEX, Pattern.CASE_INSENSITIVE);
    static final String CSS_SELECT_SUM_COL1_PATTERN = "div[class~=W]:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr > td:nth-child(2)";
    static final String CSS_SELECT_SUM_COL2_PATTERN = "table[class~=M] > tbody:nth-child(1) > tr > td:nth-child(2)";
    static final String CSS_SELECT_SUM_CURR_PATTERN = "div[class~=C] > span";
    static final String FORWARD_DIVIDEND_REGEX = "(\\d+\\.\\d*)\\s+\\((\\d+\\.\\d*)%\\)";
    static final Pattern FORWARD_DIVIDEND_PATTERN = Pattern.compile(FORWARD_DIVIDEND_REGEX, Pattern.CASE_INSENSITIVE);
    static final String CSS_SELECT_COMP_NAME_PATTERN = "h1[class~=D]";
    static final String COMP_NAME_REGEX = "\\w+\\s-\\s(.+)";
    static final Pattern COMP_NAME_PATTERN = Pattern.compile(COMP_NAME_REGEX, Pattern.CASE_INSENSITIVE);
    static final String COMP_NAME_O2_REGEX = "(.+)\\s\\(.+\\)";
    static final Pattern COMP_NAME_O2_PATTERN = Pattern.compile(COMP_NAME_O2_REGEX, Pattern.CASE_INSENSITIVE);  // company name pattern (option 2)

    Optional<String> symbol;
    Optional<Integer> timeoutMillis;
    Optional<Exception> lastException;

    /**
     * Constructor of Summary Quote Controller
     */
    public SummaryQuoteCtrl() {
        this.symbol = Optional.empty();
        this.timeoutMillis = Optional.of(TIMEOUT_MILLIS);
        this.lastException = Optional.empty();
    }

    /**
     * Set quote code symbol of stock
     * @param quoteCode quote code symbol
     * @return  the configured controller
     */
    public SummaryQuoteCtrl symbol(String quoteCode) {
        Objects.requireNonNull(quoteCode, "Quote symbol must exist for lookup.");
        this.symbol = Optional.of(quoteCode).map(String::toUpperCase);
        return this;
    }

    /**
     * Set read and connection timeout for HTTP connection
     * @param timeoutMillis timeout in milliseconds
     * @return  the configured controller
     */
    public SummaryQuoteCtrl timeout(Integer timeoutMillis) {
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
     * Get summary quote
     * @return  Summary quote
     */
    public SummaryQuote getSummaryQuote() {
        try {
            HttpResponse<String> response = WebClientHelper.getHttpResponse(getTargetUrl(), this.timeoutMillis.orElse(TIMEOUT_MILLIS));
            if (response.statusCode() == HTTP_OK) {
                return getSummaryQuoteFromBody(response.body());
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
     * Asynchrously get summary quote
     * @return  CompletableFuture of summary quote
     */
    public CompletableFuture<SummaryQuote> getSummaryQuoteAsync() {
        try {
            return WebClientHelper.getHttpResponseAsync(getTargetUrl(), this.timeoutMillis.orElse(TIMEOUT_MILLIS))
                    .thenApply(HttpResponse::body)
                    .thenApply(this::getSummaryQuoteFromBody);
        } catch (Exception ex) {
            this.lastException = Optional.of(ex);
            return CompletableFuture.failedFuture(ex);
        }
    }

    private SummaryQuote getSummaryQuoteFromBody(String body) {
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

        var htmlCurrency = htmlDoc.select(CSS_SELECT_SUM_CURR_PATTERN).get(2).text();   // the second element is about currency
        var matcherCurrency = SUMM_CURRENCY_PATTERN.matcher(htmlCurrency);
        var stockExchange = "N/A";
        var currencyCode = "N/A";
        if (matcherCurrency.find()) {
            stockExchange = matcherCurrency.group(1);
            currencyCode = matcherCurrency.group(2);
        }

        var firstColElements = htmlDoc.select(CSS_SELECT_SUM_COL1_PATTERN);
        var previousPrice = StringUtil.getBigDecimal(firstColElements.get(0).text());
        var openPrice = StringUtil.getBigDecimal(firstColElements.get(1).text());
        var bidPriceCount = firstColElements.get(2).text();
        var matcherBid = BID_ASK_PATTERN.matcher(bidPriceCount);
        var bidPrice = new BigDecimal(0);
        var bidCount = Integer.valueOf(0);
        if (matcherBid.find()) {
            bidPrice = StringUtil.getBigDecimal(matcherBid.group(1));
            bidCount = StringUtil.getInteger(matcherBid.group(2));
        }
        var askPriceCount = firstColElements.get(3).text();
        var matcherAsk = BID_ASK_PATTERN.matcher(askPriceCount);
        var askPrice = new BigDecimal(0);
        var askCount = Integer.valueOf(0);
        if (matcherAsk.find()) {
            askPrice = StringUtil.getBigDecimal(matcherAsk.group(1));
            askCount = StringUtil.getInteger(matcherAsk.group(2));
        }
        var dayPriceRange = firstColElements.get(4).text();
        var matcherDayRange = PRICE_RANGE_PATTERN.matcher(dayPriceRange);
        var dayLow = new BigDecimal(0);
        var dayHigh = new BigDecimal(0);
        if (matcherDayRange.find()) {
            dayLow = StringUtil.getBigDecimal(matcherDayRange.group(1));
            dayHigh = StringUtil.getBigDecimal(matcherDayRange.group(2));
        }
        var wk52PriceRange = firstColElements.get(5).text();
        var matcher52WkRange = PRICE_RANGE_PATTERN.matcher(wk52PriceRange);
        var fiftyTwoWeekLow = new BigDecimal(0);
        var fiftyTwoWeekHigh = new BigDecimal(0);
        if (matcher52WkRange.find()) {
            fiftyTwoWeekLow = StringUtil.getBigDecimal(matcher52WkRange.group(1));
            fiftyTwoWeekHigh = StringUtil.getBigDecimal(matcher52WkRange.group(2));
        }
        var volume = Long.valueOf(firstColElements.get(6).text().replace(",", ""));
        var averageVolume = Long.valueOf(firstColElements.get(7).text().replace(",", ""));

        var secondColElements = htmlDoc.select(CSS_SELECT_SUM_COL2_PATTERN);
        var marketCapAndUnit = StringUtil.getBigDecimalAndUnit(secondColElements.get(0).text());
        var beta = StringUtil.getBigDecimal(secondColElements.get(1).text());
        var peRatio = StringUtil.getBigDecimal(secondColElements.get(2).text());
        var eps = StringUtil.getBigDecimal(secondColElements.get(3).text());
        var earningsDate = StringUtil.getLocalDate(secondColElements.get(4).text());
        var forwardDividendYield = secondColElements.get(5).text();
        var matcherForwardDividendYield = FORWARD_DIVIDEND_PATTERN.matcher(forwardDividendYield);
        var forwardDividend = new BigDecimal(0);
        var forwardYieldPercent = new BigDecimal(0);
        if (matcherForwardDividendYield.find()) {
            forwardDividend = StringUtil.getBigDecimal(matcherForwardDividendYield.group(1));
            forwardYieldPercent = StringUtil.getBigDecimal(matcherForwardDividendYield.group(2));
        }
        var exDividendDate = StringUtil.getLocalDate(secondColElements.get(6).text());;
        var oneYearEst = StringUtil.getBigDecimal(secondColElements.get(7).text());

        return new SummaryQuote(
                this.symbol.get()
                , LocalDateTime.now()
                , companyName
                , stockExchange
                , currencyCode
                , previousPrice
                , openPrice
                , bidPrice
                , bidCount
                , askPrice
                , askCount
                , dayLow
                , dayHigh
                , fiftyTwoWeekLow
                , fiftyTwoWeekHigh
                , volume
                , averageVolume
                , marketCapAndUnit
                , beta
                , peRatio
                , eps
                , earningsDate
                , forwardDividend
                , forwardYieldPercent
                , exDividendDate
                , oneYearEst
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
                SUMM_QUOTE_URL_PATTERN
                , getQuoteCode()
        );
    }
}
