package com.categorize.model;

import java.util.List;

/**
 * Represents a category with its name and associated keywords.
 * Used for data transfer and serialization of category information.
 */
public class CategoryData {
    private String name;
    private List<String> keywords;

    public CategoryData() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }
}
