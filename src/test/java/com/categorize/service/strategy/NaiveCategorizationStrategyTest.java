package com.categorize.service.strategy;

import com.categorize.dto.UrlCategoryResult;
import com.categorize.model.Category;
import com.categorize.model.CategoryKeyword;
import com.categorize.model.WebPageContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NaiveCategorizationStrategyTest {

    private NaiveCategorizationStrategy strategy;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        strategy = new NaiveCategorizationStrategy();

        Category technology = new Category("Technology");
        technology.addKeyword(new CategoryKeyword("computer"));
        technology.addKeyword(new CategoryKeyword("software"));

        Category sports = new Category("Sports");
        sports.addKeyword(new CategoryKeyword("football"));
        sports.addKeyword(new CategoryKeyword("basketball"));

        categories = Arrays.asList(technology, sports);
    }

    @Test
    void testCategorizeWithSingleMatch() {
        WebPageContent webPage = new WebPageContent("http://example.com", "This page is about computer programming.");
        List<UrlCategoryResult> results = strategy.categorize(categories, Arrays.asList(webPage));

        assertEquals(1, results.size());
        assertEquals("http://example.com", results.get(0).getUrl());
        assertEquals(1, results.get(0).getCategories().size());
        assertTrue(results.get(0).getCategories().contains("Technology"));
    }

    @Test
    void testCategorizeWithMultipleMatches() {
        WebPageContent webPage = new WebPageContent("http://example.com", "This page is about computer software and basketball.");
        List<UrlCategoryResult> results = strategy.categorize(categories, Arrays.asList(webPage));

        assertEquals(1, results.size());
        assertEquals("http://example.com", results.get(0).getUrl());
        assertEquals(2, results.get(0).getCategories().size());
        assertTrue(results.get(0).getCategories().contains("Technology"));
        assertTrue(results.get(0).getCategories().contains("Sports"));
    }

    @Test
    void testCategorizeWithNoMatch() {
        WebPageContent webPage = new WebPageContent("http://example.com", "This page is about cooking and recipes.");
        List<UrlCategoryResult> results = strategy.categorize(categories, Arrays.asList(webPage));

        assertEquals(1, results.size());
        assertEquals("http://example.com", results.get(0).getUrl());
        assertTrue(results.get(0).getCategories().isEmpty());
    }

    @Test
    void testCategorizeMultiplePages() {
        WebPageContent page1 = new WebPageContent("http://example1.com", "This page is about computer programming.");
        WebPageContent page2 = new WebPageContent("http://example2.com", "This page is about football and basketball.");
        WebPageContent page3 = new WebPageContent("http://example3.com", "This page is about cooking and recipes.");

        List<UrlCategoryResult> results = strategy.categorize(categories, Arrays.asList(page1, page2, page3));

        assertEquals(3, results.size());
        
        assertEquals("http://example1.com", results.get(0).getUrl());
        assertEquals(1, results.get(0).getCategories().size());
        assertTrue(results.get(0).getCategories().contains("Technology"));

        assertEquals("http://example2.com", results.get(1).getUrl());
        assertEquals(1, results.get(1).getCategories().size());
        assertTrue(results.get(1).getCategories().contains("Sports"));

        assertEquals("http://example3.com", results.get(2).getUrl());
        assertTrue(results.get(2).getCategories().isEmpty());
    }
}
