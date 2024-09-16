package com.categorize.model;

/**
 * Represents the content of a web page.
 */
public class WebPageContent {
    private String url;
    private String content;

    public WebPageContent(String url, String content) {
        this.url = url;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
