package com.categorize.service;

import com.categorize.model.WebPageContent;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.parser.StreamParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * Service for retrieving and cleaning web page content using jsoup's Parser.
 * This service provides methods to fetch and clean HTML content from multiple URLs.
 */
@Service
public class WebPageService {
    private static final Logger          log = Logger.getLogger(WebPageService.class.getName());
    private final        long            urlFetchTimeoutMs;
    private final        ExecutorService threadPool;


    public WebPageService(
            @Value("${url.fetch.timeout:5000}") long urlFetchTimeoutMs) {
        this.urlFetchTimeoutMs = urlFetchTimeoutMs;
        this.threadPool        = Executors.newCachedThreadPool();
    }

    /**
     * Retrieves and cleans the content of multiple web pages in parallel.
     * <p>
     * This method fetches web page content for multiple URLs concurrently, applying a timeout
     * to limit the overall execution time. It uses CompletableFuture for asynchronous processing.
     *
     * @param urls List of URLs to retrieve content from
     * @return List of WebPageContent objects containing URL and cleaned content.
     * Each WebPageContent object contains the original URL and its cleaned text content.
     * Only successfully fetched and processed pages within the timeout period are included.
     */
    public List<WebPageContent> getWebPagesContent(List<String> urls) {

        List<CompletableFuture<Optional<WebPageContent>>> futures;
        futures = urls.parallelStream()
                      .map(url -> CompletableFuture.supplyAsync(() -> fetchWebPageContent(url), threadPool))
                      .collect(Collectors.toList());

        awaitCompletionWithTimeout(futures, urlFetchTimeoutMs);

        // return the completed results within the timeout period
        List<WebPageContent> completed = futures.stream()
                                                .filter(WebPageService::isFinished)
                                                .map(f -> f.getNow(null).get())
                                                .collect(Collectors.toList());

        return completed;
    }

    /**
     * Checks if a CompletableFuture has failed.
     *
     * @param f The CompletableFuture to check
     * @return true if the future has failed, false otherwise
     */
    private static boolean isFailed(CompletableFuture<Optional<WebPageContent>> f) {
        return f.isCompletedExceptionally() || f.isCancelled() || (f.isDone() && !f.getNow(null).isPresent());
    }

    /**
     * Checks if a CompletableFuture has completed successfully.
     *
     * @param f The CompletableFuture to check
     * @return true if the future has completed successfully, false otherwise
     */
    private static boolean isSuccess(CompletableFuture<Optional<WebPageContent>> f) {
        return f.isDone() && (!f.isCancelled() && !f.isCompletedExceptionally()) && f.getNow(null).isPresent();
    }

    /**
     * Checks if a CompletableFuture has finished processing successfully and returned a result.
     *
     * @param f The CompletableFuture to check
     * @return true if the future has finished processing, false otherwise
     */
    private static boolean isFinished(CompletableFuture<Optional<WebPageContent>> f) {
        return f.isDone() && (!f.isCancelled() && !f.isCompletedExceptionally())
               // we assume that if it's done without exception then the result is not null
               && f.getNow(null).isPresent();
    }

    /**
     * Waits for the completion of a list of CompletableFutures with a timeout.
     *
     * @param futures The list of CompletableFutures to wait for
     * @param timeout The maximum time to wait in milliseconds
     */
    private static void awaitCompletionWithTimeout(List<CompletableFuture<Optional<WebPageContent>>> futures, Long timeout) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime <= timeout) {
            try {
                CompletableFuture[] unfinished = futures.stream().filter(f -> !(isSuccess(f) || isFailed(f))).toArray(CompletableFuture[]::new);
                CompletableFuture.anyOf(unfinished).get(500L, TimeUnit.MILLISECONDS);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                // swallow these exceptions as they mean either a timeout or a failed process
                // which shouldn't happen because fetchWebPageContent is wrapped with try-catch
            }
        }
    }


    /**
     * Fetches and cleans the content of a single web page using Jsoup's StreamParser.
     * This method performs the following steps:
     * 1. Connects to the URL and retrieves the HTML content.
     * 2. Uses Jsoup's StreamParser to parse the HTML, extract and concatenate all text nodes in a single pass, ignoring HTML tags and other elements.
     * 3. Wraps the result in a WebPageContent object.
     *
     * @param url The URL of the web page to fetch and clean
     * @return An Optional containing the WebPageContent with cleaned text, or empty if an error occurred
     */
    private Optional<WebPageContent> fetchWebPageContent(String url) {
        try {
            String htmlContent = Jsoup.connect(url)
                                      .timeout((int) urlFetchTimeoutMs)
                                      .execute()
                                      .body();
            StringBuilder cleanedContent = new StringBuilder();
            StreamParser streamParser = new StreamParser(Parser.htmlParser());

            streamParser.parse(new StringReader(htmlContent), url).iterator().forEachRemaining(element -> {
                cleanedContent.append(element.text());
                cleanedContent.append(" ");
            });

            return Optional.of(new WebPageContent(url, cleanedContent.toString()));
        } catch (IOException e) {
            log.log(Level.WARNING, "Error fetching or parsing web page content for URL: " + url, e);
            return Optional.empty();
        }
    }
}
