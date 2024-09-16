package com.categorize.model;

import java.util.List;

/**
 * Represents a collection of category data.
 * This class is used for serialization and deserialization of categories from JSON.
 */
public class CategoriesData {
    private List<CategoryData> categories;

    public CategoriesData() {}

    public List<CategoryData> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryData> categories) {
        this.categories = categories;
    }
}
