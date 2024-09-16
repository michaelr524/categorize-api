package com.categorize.service.strategy;

/**
 * Enum representing different types of categorization strategies.
 * This enum is used to select the appropriate strategy for content categorization.
 */
public enum CategorizationStrategyType {
    /**
     * A simple, straightforward categorization strategy.
     */
    NAIVE,

    /**
     * A categorization strategy using regular expressions for pattern matching.
     */
    REGEX;

    public static CategorizationStrategyType fromString(String value) {
        for (CategorizationStrategyType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown strategy: " + value);
    }
}
