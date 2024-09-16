package com.categorize.model;

/**
 * Represents a keyword associated with a category.
 * The keyword is stored in lowercase for case-insensitive matching.
 */
public class CategoryKeyword {
    private String keyword;

    public CategoryKeyword(String keyword) {
        this.keyword = keyword.toLowerCase();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword.toLowerCase();
    }
}
