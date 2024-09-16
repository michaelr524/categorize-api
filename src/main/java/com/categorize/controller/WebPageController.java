package com.categorize.controller;

import com.categorize.model.WebPageContent;
import com.categorize.service.WebPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for handling web page content requests.
 */
@RestController
public class WebPageController {

    private final WebPageService webPageService;

    @Autowired
    public WebPageController(WebPageService webPageService) {
        this.webPageService = webPageService;
    }

    /**
     * Retrieves the content of multiple web pages.
     *
     * @param urls List of URLs to retrieve content from
     * @return List of WebPageContent objects containing URL and content
     */
    @PostMapping("/webpages")
    public List<WebPageContent> getWebPagesContent(@RequestBody List<String> urls) {
        return webPageService.getWebPagesContent(urls);
    }
}
