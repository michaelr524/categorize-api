package com.categorize.service.strategy;

import com.categorize.dto.UrlCategoryResult;
import com.categorize.model.Category;
import com.categorize.model.WebPageContent;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implements a regex-based approach to categorization.
 * This strategy compiles category keywords into regex patterns for efficient matching.
 * <p>
 * Implementation complexity:
 * - Time complexity: O(N * M), where N is the text length and M is the number of categories.
 * The regex matching is generally more efficient than naive string matching, especially for multiple keywords.
 */
public class RegexCategorizationStrategy implements CategorizationStrategy {
    private final Map<String, Pattern> categoryToPattern;
    private final ExecutorService executorService;

    /**
     * Constructs the regex strategy with the given categories.
     * Compiles the keywords of each category into a regex pattern.
     *
     * @param categories List of categories to compile patterns from
     */
    public RegexCategorizationStrategy(List<Category> categories) {
        categoryToPattern = new HashMap<>();
        for (Category category : categories) {
            String regex = category.getKeywords().stream().map(keyword -> Pattern.quote(keyword.getKeyword())).collect(Collectors.joining("|"));
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            categoryToPattern.put(category.getName(), pattern);
        }
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Categorizes a list of web pages based on the specified categories.
     *
     * @param categories List of categories to match against
     * @param webPages   List of web page contents to categorize
     * @return List of UrlCategoryResult objects containing the categorization results for each web page
     */
    @Override
    public List<UrlCategoryResult> categorize(List<Category> categories, List<WebPageContent> webPages) {
        Map<String, Pattern> relevantPatterns = categories.stream()
            .filter(category -> categoryToPattern.containsKey(category.getName()))
            .collect(Collectors.toMap(Category::getName, category -> categoryToPattern.get(category.getName())));

        List<CompletableFuture<UrlCategoryResult>> futures = webPages.stream()
            .map(webPage -> CompletableFuture.supplyAsync(() -> categorizeWebPage(webPage, relevantPatterns), executorService))
            .collect(Collectors.toList());

        return futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
    }

    /**
     * Categorizes a single web page using the provided patterns.
     *
     * @param webPage The WebPageContent object containing the URL and content to categorize
     * @param relevantPatterns A map of category names to their corresponding regex patterns
     * @return A UrlCategoryResult object containing the URL and its matching categories
     */
    private UrlCategoryResult categorizeWebPage(WebPageContent webPage, Map<String, Pattern> relevantPatterns) {
        Set<String> matchedCategories = new HashSet<>();
        String content = webPage.getContent();
        for (Map.Entry<String, Pattern> entry : relevantPatterns.entrySet()) {
            if (entry.getValue().matcher(content).find()) {
                matchedCategories.add(entry.getKey());
            }
        }
        return new UrlCategoryResult(webPage.getUrl(), new ArrayList<>(matchedCategories));
    }
}
