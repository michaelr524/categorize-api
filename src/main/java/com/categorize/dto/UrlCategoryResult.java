package com.categorize.dto;

import java.util.List;

/**
 * DTO for individual URL categorization results.
 * Contains a URL and its matching categories.
 */
public class UrlCategoryResult {
    private String url;
    private List<String> categories;

    public UrlCategoryResult(String url, List<String> categories) {
        this.url = url;
        this.categories = categories;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
