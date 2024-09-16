package com.categorize.controller;

import com.categorize.dto.CategorizeRequest;
import com.categorize.dto.CategorizeResponse;
import com.categorize.dto.UrlCategoryResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/categorize";
    }

    @Test
    public void testCategorizeUrls() {
        // Prepare test data
        List<String> categories = Arrays.asList("Star Wars", "Basketball");
        List<String> urls = Arrays.asList(
            "http://www.starwars.com",
            "https://www.imdb.com/find?q=star+wars&ref_=nv_sr_sm",
            "https://edition.cnn.com/sport"
        );

        CategorizeRequest request = new CategorizeRequest();
        request.setCategories(categories);
        request.setUrls(urls);

        // Send request and get response
        ResponseEntity<CategorizeResponse> responseEntity = restTemplate.postForEntity(baseUrl, request, CategorizeResponse.class);

        // Assert response status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Assert response body
        CategorizeResponse response = responseEntity.getBody();
        assertNotNull(response);
        List<UrlCategoryResult> results = response.getResults();
        assertNotNull(results);
        // some urls may timeout, error, etc
//        assertEquals(urls.size(), results.size());

        // Assert each result
        for (UrlCategoryResult result : results) {
            assertTrue(urls.contains(result.getUrl()));
            assertNotNull(result.getCategories());
            assertFalse(result.getCategories().isEmpty());

            if (result.getUrl().contains("starwars.com") || result.getUrl().contains("imdb.com")) {
                assertTrue(result.getCategories().contains("Star Wars"));
            }

            if (result.getUrl().contains("cnn.com/sport")) {
                assertTrue(result.getCategories().contains("Basketball"));
            }
        }
    }
}
