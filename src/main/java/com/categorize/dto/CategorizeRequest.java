package com.categorize.dto;

import java.util.List;

/**
 * DTO for categorization requests.
 * Contains lists of categories and URLs to be categorized.
 */
public class CategorizeRequest {
    private List<String> categories;
    private List<String> urls;

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
