package com.categorize.dto;

import java.util.List;

/**
 * DTO for categorization responses.
 * Contains a list of UrlCategoryResult objects.
 */
public class CategorizeResponse {
    private List<UrlCategoryResult> results;

    // Default constructor for Jackson deserialization
    public CategorizeResponse() {
    }

    public CategorizeResponse(List<UrlCategoryResult> results) {
        this.results = results;
    }

    public List<UrlCategoryResult> getResults() {
        return results;
    }

    public void setResults(List<UrlCategoryResult> results) {
        this.results = results;
    }
}
