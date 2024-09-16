package com.categorize.service;

import com.categorize.model.Category;
import com.categorize.model.CategoryKeyword;
import com.categorize.model.WebPageContent;
import com.categorize.service.strategy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.categorize.dto.CategorizeResponse;
import com.categorize.dto.UrlCategoryResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.categorize.model.CategoriesData;
import com.categorize.model.CategoryData;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing categories and categorizing web pages.
 * This service initializes predefined categories from a JSON file and provides methods for categorizing URLs based on their content.
 * It supports multiple categorization strategies that can be configured and switched at runtime.
 */
@Service
public class CategoryService {
    private static final Logger log                  = LoggerFactory.getLogger(CategoryService.class);
    private static final String CATEGORIES_JSON_PATH = "classpath:categories.json";

    private final List<Category>         categories;
    private       CategorizationStrategy strategy;
    private final ResourceLoader         resourceLoader;
    private final ObjectMapper           objectMapper;

    /**
     * Constructs a new CategoryService.
     * Initializes the categories list by populating it with categories from a JSON file.
     * Sets up the categorization strategy based on the provided configuration.
     *
     * @param strategyName   The name of the categorization strategy to use, specified in application properties.
     * @param resourceLoader Spring's ResourceLoader for loading the categories JSON file.
     * @param objectMapper   Jackson's ObjectMapper for parsing JSON.
     */
    @Autowired
    public CategoryService(
            @Value("${categorization.strategy}") String strategyName, ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper   = objectMapper;
        this.categories     = new ArrayList<>();
        initializeCategories();
        this.strategy = createStrategy(CategorizationStrategyType.fromString(strategyName));
    }

    private CategorizationStrategy createStrategy(CategorizationStrategyType strategyType) {
        switch (strategyType) {
            case NAIVE:
                return new NaiveCategorizationStrategy();
            case REGEX:
                return new RegexCategorizationStrategy(categories);
            default:
                throw new IllegalArgumentException("Unknown strategy: " + strategyType);
        }
    }

    /**
     * Initializes categories with their respective keywords from a JSON file.
     * Reads the categories data from a predefined JSON file and creates Category objects.
     * If an error occurs during file reading or parsing, it throws a runtime exception.
     *
     * @throws RuntimeException if categories cannot be loaded from the JSON file
     */
    private void initializeCategories() {
        try (InputStream inputStream = resourceLoader.getResource(CATEGORIES_JSON_PATH).getInputStream()) {
            CategoriesData categoriesData = objectMapper.readValue(inputStream, CategoriesData.class);

            categories.addAll(categoriesData.getCategories().stream().map(this::createCategory).collect(Collectors.toList()));
        } catch (IOException e) {
            log.error("Failed to load categories from JSON file", e);
            throw new RuntimeException("Failed to initialize categories. Application cannot start.", e);
        }
    }

    /**
     * Creates a Category object from CategoryData.
     *
     * @param categoryData The CategoryData object containing the category name and keywords.
     * @return A new Category object populated with the name and keywords from the CategoryData.
     */
    private Category createCategory(CategoryData categoryData) {
        Category category = new Category(categoryData.getName());
        categoryData.getKeywords().forEach(keyword -> category.addKeyword(new CategoryKeyword(keyword)));
        return category;
    }

    /**
     * Sets the categorization strategy to be used.
     * This method allows for dynamic switching of categorization strategies at runtime.
     *
     * @param strategy The CategorizationStrategy to be used for categorization.
     */
    public void setStrategy(CategorizationStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Categorizes a list of web pages based on their content and the specified categories.
     * This method filters the categories based on the provided category names and then
     * uses the current categorization strategy to categorize the web pages.
     *
     * @param categoryNames A list of category names to consider for categorization.
     * @param webPages      A list of WebPageContent objects containing the URLs and their content.
     * @return A CategorizeResponse object containing the categorization results.
     * The results include a list of UrlCategoryResult objects, each containing
     * a URL and its matching categories.
     */
    public CategorizeResponse categorizeUrls(List<String> categoryNames, List<WebPageContent> webPages) {
        Set<String> categoryNamesSet = new HashSet<>(categoryNames);
        List<Category> selectedCategories = categories.stream()
                                                      .filter(category -> categoryNamesSet.contains(category.getName()))
                                                      .collect(Collectors.toList());

        List<UrlCategoryResult> results = strategy.categorize(selectedCategories, webPages);

        return new CategorizeResponse(results);
    }
}
