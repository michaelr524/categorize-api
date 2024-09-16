package com.categorize.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a category with a name and associated keywords.
 */
public class Category {
    private String name;
    private List<CategoryKeyword> keywords;

    public Category(String name) {
        this.name = name;
        this.keywords = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CategoryKeyword> getKeywords() {
        return keywords;
    }

    public void addKeyword(CategoryKeyword keyword) {
        this.keywords.add(keyword);
    }
}
