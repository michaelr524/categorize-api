package com.categorize.controller;

import com.categorize.dto.CategorizeRequest;
import com.categorize.service.WebPageService;
import com.categorize.service.CategoryService;
import com.categorize.model.WebPageContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import com.categorize.dto.CategorizeResponse;
import com.categorize.dto.UrlCategoryResult;

/**
 * Controller for handling category-related operations.
 * Provides an endpoint for categorizing URLs based on their content.
 */
@RestController
public class CategoryController {

    private final WebPageService webPageService;
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(WebPageService webPageService, CategoryService categoryService) {
        this.webPageService = webPageService;
        this.categoryService = categoryService;
    }

    @PostMapping("/categorize")
    /**
     * Categorizes URLs based on the provided categories.
     *
     * @param request The CategorizeRequest object containing categories and URLs to categorize
     * @return CategorizeResponse object containing the categorization results for each URL
     */
    public CategorizeResponse categorizeUrls(@RequestBody CategorizeRequest request) {
        List<WebPageContent> webPages = webPageService.getWebPagesContent(request.getUrls());
        return categoryService.categorizeUrls(request.getCategories(), webPages);
    }
}
