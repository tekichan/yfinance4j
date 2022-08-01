package self.tekichan.demo.yfinance4j.ctrl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import self.tekichan.demo.yfinance4j.model.IndexComponent;
import self.tekichan.demo.yfinance4j.model.IndexComponentInfo;
import self.tekichan.demo.yfinance4j.util.WebClientHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.HTTP_OK;
import static self.tekichan.demo.yfinance4j.YFinanceConfig.*;

/**
 * Controller class for IndexComponent
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public class IndexComponentCtrl {
    static final String INDEX_COMPONENT_URL_PATTERN = "https://finance.yahoo.com/quote/%1$s/components?p=%1$s";
    static final String COMP_CURRENCY_REGEX = "Currency\\sin\\s(\\w+)";
    static final Pattern COMP_CURRENCY_PATTERN = Pattern.compile(COMP_CURRENCY_REGEX, Pattern.CASE_INSENSITIVE);
    static final String CSS_SELECT_CURRENCY = "div[class~=My] > span:nth-child(1)";
    static final String CSS_SELECT_IDX_COMP_PATTERN = "tr.BdT > td:nth-child(%1$d)";

    Optional<String> symbol;
    Optional<Integer> timeoutMillis;
    Optional<Exception> lastException;

    /**
     * Constructor of Index Component Controller
     */
    public IndexComponentCtrl() {
        this.symbol = Optional.empty();
        this.timeoutMillis = Optional.of(TIMEOUT_MILLIS);
        this.lastException = Optional.empty();
    }

    /**
     * Set quote code symbol of stock index
     * @param quoteCode quote code symbol
     * @return  the configured IndexComponentCtrl
     */
    public IndexComponentCtrl symbol(String quoteCode) {
        Objects.requireNonNull(quoteCode, "Quote symbol must exist for lookup.");
        this.symbol = Optional.of(quoteCode).filter(q -> q.startsWith("^")).map(String::toUpperCase);
        return this;
    }

    /**
     * Set read and connection timeout for HTTP connection
     * @param timeoutMillis timeout in milliseconds
     * @return  the configured IndexComponentCtrl
     */
    public IndexComponentCtrl timeout(Integer timeoutMillis) {
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
     * Get the information of components of given stock index
     * @return  the information of index components
     */
    public IndexComponentInfo getIndexComponentInfo() {
        try {
            HttpResponse<String> response = WebClientHelper.getHttpResponse(getTargetUrl(), this.timeoutMillis.orElse(TIMEOUT_MILLIS));
            if (response.statusCode() == HTTP_OK) {
                return getComponentInfoFromBody(response.body());
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
     * Asynchronously get the information of components of given stock index
     * @return  CompletableFuture of the information of index components
     */
    public CompletableFuture<IndexComponentInfo> getIndexComponentInfoAsync() {
        try {
            return WebClientHelper.getHttpResponseAsync(getTargetUrl(), this.timeoutMillis.orElse(TIMEOUT_MILLIS))
                    .thenApply(HttpResponse::body)
                    .thenApply(this::getComponentInfoFromBody);
        } catch (Exception ex) {
            this.lastException = Optional.of(ex);
            return CompletableFuture.failedFuture(ex);
        }
    }

    private IndexComponentInfo getComponentInfoFromBody(String body) {
        Document htmlDoc = Jsoup.parse(body);
        String htmlCurrency = htmlDoc.select(CSS_SELECT_CURRENCY).first().text();
        Matcher matcherCurrency = COMP_CURRENCY_PATTERN.matcher(htmlCurrency);
        String currencyCode = matcherCurrency.find() ? matcherCurrency.group(1) : "N/A";
        List<String> symbolList = getColumnSelector(htmlDoc, 1);
        List<String> companyList = getColumnSelector(htmlDoc, 2);
        List<String> lastPriceList = getColumnSelector(htmlDoc, 3);
        List<String> changeList = getColumnSelector(htmlDoc, 4);
        List<String> percentChangeList = getColumnSelector(htmlDoc, 5);
        List<String> volumeList = getColumnSelector(htmlDoc, 6);
        List<IndexComponent> componentList = new ArrayList<>();
        for(int i=0; i<symbolList.size(); i++) {
            try {
                componentList.add(
                        new IndexComponent(
                                symbolList.get(i)
                                , companyList.get(i)
                                , lastPriceList.get(i).replace(",", "")
                                , changeList.get(i).replace(",", "")
                                , percentChangeList.get(i).replaceAll("[%,]", "")
                                , volumeList.get(i).replace(",", "")
                        )
                );
            } catch (Exception ex) {
                // skip if error
            }
        }
        return new IndexComponentInfo(
                this.symbol.get()
                , LocalDateTime.now()
                , currencyCode
                , componentList
        );
    }

    private List<String> getColumnSelector(Document htmlDoc, int columnIdx) {
        return htmlDoc
                .select(CSS_SELECT_IDX_COMP_PATTERN.formatted(columnIdx))
                .stream()
                .map(ele -> ele.text())
                .toList();
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
                INDEX_COMPONENT_URL_PATTERN
                , getQuoteCode()
        );
    }
}
