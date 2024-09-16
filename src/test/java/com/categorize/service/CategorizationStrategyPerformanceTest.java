package com.categorize.service;

import com.categorize.dto.UrlCategoryResult;
import com.categorize.model.Category;
import com.categorize.model.CategoryKeyword;
import com.categorize.model.WebPageContent;
import com.categorize.service.strategy.CategorizationStrategy;
import com.categorize.service.strategy.NaiveCategorizationStrategy;
import com.categorize.service.strategy.RegexCategorizationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategorizationStrategyPerformanceTest {

    private              List<Category>       categories;
    private              double               totalMatches;
    private              List<WebPageContent> webPages;
    private static final int                  NUM_CATEGORIES            = 20;
    private static final int                  NUM_WEB_PAGES             = 20;
    private static final int                  MAX_KEYWORDS_PER_CATEGORY = 1000;
    private static final int                  MAX_WORDS_PER_KEYWORD     = 6;
    private static final int                  MAX_WORDS_PER_WEBPAGE     = 3_000;
    public static final  int                  WARM_UP_RUNS              = 1_000;
    public static final  int                  MEASURED_RUNS             = 2_000;

    @BeforeEach
    void setUp() {
        categories = generateCategories();
        webPages   = generateWebPages();
    }

    @Test
    void compareStrategyPerformance() {
        List<CategorizationStrategy> strategies = Arrays.asList(new NaiveCategorizationStrategy(), new RegexCategorizationStrategy(categories));

        for (CategorizationStrategy strategy : strategies) {
            // Warm-up phase
            for (int i = 0; i < WARM_UP_RUNS; i++) {
                strategy.categorize(categories, webPages);
            }

            // Measurement phase
            long                    totalDuration = 0;
            List<UrlCategoryResult> result        = null;
            totalMatches = 0;

            for (int i = 0; i < MEASURED_RUNS; i++) {
                long startTime = System.nanoTime();
                result = strategy.categorize(categories, webPages);
                long endTime = System.nanoTime();
                totalDuration += (endTime - startTime);
                totalMatches += result.stream().mapToInt(r -> r.getCategories().size()).sum();
            }

            long avgDuration = totalDuration / MEASURED_RUNS / 1_000_000; // Convert to milliseconds

            System.out.printf("%s performance:\n", strategy.getClass().getSimpleName());
            System.out.printf("  Average duration: %d ms over %d runs (after %d warm-up runs)\n", avgDuration, MEASURED_RUNS, WARM_UP_RUNS);
            System.out.printf("  Test parameters:\n");
            System.out.printf("    Number of categories: %d\n", NUM_CATEGORIES);
            System.out.printf("    Number of web pages: %d\n", NUM_WEB_PAGES);
            System.out.printf("    Max keywords per category: %d\n", MAX_KEYWORDS_PER_CATEGORY);
            System.out.printf("    Max words per keyword: %d\n", MAX_WORDS_PER_KEYWORD);
            System.out.printf("    Max words per web page: %d\n", MAX_WORDS_PER_WEBPAGE);

            System.out.printf("  Total number of category matches: %.2f\n", totalMatches);
            System.out.println();
        }
    }

    private List<Category> generateCategories() {
        return IntStream.range(0, NUM_CATEGORIES).mapToObj(i -> {
            Category category    = new Category("Category" + i);
            int      numKeywords = new Random().nextInt(MAX_KEYWORDS_PER_CATEGORY) + 1;
            for (int j = 0; j < numKeywords; j++) {
                category.addKeyword(new CategoryKeyword(generateRandomPhrase(MAX_WORDS_PER_KEYWORD)));
            }
            return category;
        }).collect(Collectors.toList());
    }

    private List<WebPageContent> generateWebPages() {
        return IntStream.range(0, NUM_WEB_PAGES)
                        .mapToObj(i -> new WebPageContent("http://example.com/page" + i, generateRandomPhrase(MAX_WORDS_PER_WEBPAGE)))
                        .collect(Collectors.toList());
    }

    private String generateRandomPhrase(int maxWords) {
        Random random   = new Random();
        int    numWords = random.nextInt(maxWords) + 1;
        return IntStream.range(0, numWords).mapToObj(i -> generateRandomWord()).collect(Collectors.joining(" "));
    }

    private String generateRandomWord() {
        String        chars  = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb     = new StringBuilder();
        Random        random = new Random();
        int           length = random.nextInt(10) + 1;
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
