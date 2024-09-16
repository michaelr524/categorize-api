package com.categorize.controller;

import com.categorize.model.WebPageContent;
import com.categorize.service.WebPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebPageControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WebPageService webPageService;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/webpages";
    }

    @Test
    public void testGetWebPagesContent() {
        List<String> urls = Arrays.asList(
            "http://www.msn.com/en-nz/travel/tripideas/70-of-the-planets-most-breathtaking-sights/ss-AAIUpDp",
            "https://www.radiosport.co.nz/sport-news/rugby/accident-or-one-last-dig-eddie-jones-reveals-hansens-next-job/",
            "https://www.bbc.com",
            "https://www.tvblog.it/post/1681999/valerio-fabrizio-salvatori-gli-inseparabili-chi-sono-pechino-express-2020",
            "http://edition.cnn.com/"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> request = new HttpEntity<>(urls, headers);

        ResponseEntity<WebPageContent[]> response = restTemplate.postForEntity(baseUrl, request, WebPageContent[].class);

        assertNotNull(response.getBody());
        // some urls may timeout, error, etc
//        assertEquals(urls.size(), response.getBody().length);

        for (int i = 0; i < urls.size(); i++) {
            WebPageContent content = response.getBody()[i];
            assertNotNull(content);
            assertEquals(urls.get(i), content.getUrl());
            assertNotNull(content.getContent());
        }
    }
}
