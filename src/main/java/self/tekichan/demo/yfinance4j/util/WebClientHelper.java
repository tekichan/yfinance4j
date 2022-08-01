package self.tekichan.demo.yfinance4j.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MILLIS;

/**
 * A helper class to handle
 * essential operations for a web client
 *
 * @author Teki Chan
 * @since 1 Jul 2022
 */
public final class WebClientHelper {
    /**
     * Build HttpRequest object
     * @param targetUrl target URL
     * @param timeoutMillis read timeout in milliseconds
     * @return  HttpRequest object
     * @throws URISyntaxException   {@link URISyntaxException}
     */
    public static HttpRequest buildHttpRequest(String targetUrl, int timeoutMillis)
            throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(targetUrl))
                .timeout(Duration.of(timeoutMillis, MILLIS))
                .GET()
                .build();
    }

    /**
     * Build HttpClient object
     * @param timeoutMillis connection timeout in milliseconds
     * @return  HttpClient object
     */
    public static HttpClient buildHttpClient(int timeoutMillis) {
        return HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.of(timeoutMillis, MILLIS))
                .build();
    }

    /**
     * Get HttpResponse object
     * @param targetUrl target URL
     * @param timeoutMillis     read and connection timeout
     * @return  HttpResponse object
     * @throws IOException  {@link IOException}
     * @throws InterruptedException {@link InterruptedException}
     * @throws URISyntaxException   {@link URISyntaxException}
     */
    public static HttpResponse<String> getHttpResponse(String targetUrl, int timeoutMillis)
            throws IOException, InterruptedException, URISyntaxException {
        return buildHttpClient(timeoutMillis)
                .send(
                        buildHttpRequest(targetUrl, timeoutMillis)
                        , HttpResponse.BodyHandlers.ofString()
                );
    }

    /**
     * Asynchronously get CompletableFuture - HttpResponse object
     * @param targetUrl     target URL
     * @param timeoutMillis     read and connection timeout
     * @return  CompletableFuture of HttpResponse
     * @throws URISyntaxException   {@link URISyntaxException}
     */
    public static CompletableFuture<HttpResponse<String>> getHttpResponseAsync(String targetUrl, int timeoutMillis)
            throws URISyntaxException {
        return buildHttpClient(timeoutMillis)
                .sendAsync(
                        buildHttpRequest(targetUrl, timeoutMillis)
                        , HttpResponse.BodyHandlers.ofString()
                );
    }

    /**
     * Download CSV to List of the class type
     * @param targetUrl     target URL
     * @param timeoutMillis read and connection timeout
     * @param mapFunc   Mapping function to convert Stream of String to the class type
     * @return  List of the class type
     * @param <T>   the class type
     * @throws URISyntaxException   {@link URISyntaxException}
     * @throws InterruptedException {@link InterruptedException}
     * @throws IOException  {@link IOException}
     */
    public static <T> List<T> downloadCsvToList(String targetUrl, int timeoutMillis, Function<Stream<String>, List<T>> mapFunc)
            throws URISyntaxException, InterruptedException, IOException {
        HttpResponse<Stream<String>> response =
                buildHttpClient(timeoutMillis)
                .send(
                        buildHttpRequest(targetUrl, timeoutMillis)
                        , HttpResponse.BodyHandlers.ofLines()
                );
        return mapFunc.apply(response.body());
    }

    /**
     * Asynchronously download CSV to List of the class type
     * @param targetUrl target URL
     * @param timeoutMillis read and connection timeout
     * @param mapFunc   Mapping function to convert Stream of String to the class type
     * @return  CompletableFuture of List of the class type
     * @param <T>   the class type
     * @throws URISyntaxException   {@link URISyntaxException}
     */
    public static <T> CompletableFuture<List<T>> downloadCsvToListAsync(String targetUrl, int timeoutMillis, Function<Stream<String>, List<T>> mapFunc)
            throws URISyntaxException {
        return buildHttpClient(timeoutMillis)
                .sendAsync(
                        buildHttpRequest(targetUrl, timeoutMillis)
                        , HttpResponse.BodyHandlers.ofLines()
                )
                .thenApply(HttpResponse::body)
                .thenApply(mapFunc);
    }
}
