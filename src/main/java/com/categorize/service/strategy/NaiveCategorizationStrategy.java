package com.categorize.service.strategy;

import com.categorize.model.Category;
import com.categorize.model.WebPageContent;
import com.categorize.dto.UrlCategoryResult;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Implements a naive approach to categorization.
 * This strategy simply checks if any keyword of a category is contained in the web page content.
 * <p>
 * Implementation complexity:
 * - Time complexity: O(N * M * K), where N is the text length, M is the number of categories,
 * and K is the maximum keyword length.
 */
public class NaiveCategorizationStrategy implements CategorizationStrategy {
    private final ExecutorService executorService;

    public NaiveCategorizationStrategy() {
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Categorizes web pages using a simple string matching approach.
     *
     * @param categories List of categories to match against
     * @param webPages   List of web pages to categorize
     * @return List of UrlCategoryResult containing the categorization results
     */
    @Override
    public List<UrlCategoryResult> categorize(List<Category> categories, List<WebPageContent> webPages) {
        List<UrlCategoryResult> results = webPages.stream()
                                                  .map(webPage -> CompletableFuture.supplyAsync(() -> categorizeSinglePage(categories, webPage),
                                                                                                executorService))
                                                  .map(CompletableFuture::join)
                                                  .collect(Collectors.toList());

        return results;
    }

    private static UrlCategoryResult categorizeSinglePage(List<Category> categories, WebPageContent webPage) {
        String content = webPage.getContent().toLowerCase();
        List<String> matchedCategories = categories.stream()
                                                   .filter(category -> category.getKeywords()
                                                                               .stream()
                                                                               .anyMatch(keyword -> content.contains(keyword.getKeyword())))
                                                   .map(Category::getName)
                                                   .collect(Collectors.toList());
        return new UrlCategoryResult(webPage.getUrl(), matchedCategories);
    }
}
