package com.categorize.service.strategy;

import com.categorize.model.Category;
import com.categorize.model.WebPageContent;
import com.categorize.dto.UrlCategoryResult;
import java.util.List;

/**
 * Defines the contract for categorization strategies.
 * Implementations of this interface provide different algorithms for categorizing web pages.
 */
public interface CategorizationStrategy {
    /**
     * Categorizes a list of web pages based on the given categories.
     *
     * @param categories List of categories to match against
     * @param webPages List of web pages to categorize
     * @return List of UrlCategoryResult containing the categorization results
     */
    List<UrlCategoryResult> categorize(List<Category> categories, List<WebPageContent> webPages);
}
