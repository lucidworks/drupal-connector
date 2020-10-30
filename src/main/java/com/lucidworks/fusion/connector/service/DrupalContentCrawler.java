package com.lucidworks.fusion.connector.service;

import com.lucidworks.fusion.connector.exception.ServiceException;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

/**
 * Drupal Content Crawler can create a Map with all links and content from them.
 */
@Slf4j
public class DrupalContentCrawler {

    private boolean processFinished = false;
    private List<String> drupalUrls;
    private Map<String, String> visitedUrls;
    private DrupalHttpClient drupalHttpClient;
    private ContentService contentService;
    private Map<String, TopLevelJsonapi> topLevelJsonapiMap;

    /**
     * Constructor for Crawler
     *
     * @param drupalUrl        the url for the first GET request
     * @param drupalHttpClient the Http Client object
     * @param contentService   the content service class
     */
    public DrupalContentCrawler(String drupalUrl, ContentService contentService, DrupalHttpClient drupalHttpClient) {
        this.drupalUrls = new ArrayList<>(Arrays.asList(drupalUrl));
        this.visitedUrls = new HashMap<>();
        this.topLevelJsonapiMap = new HashMap<>();
        this.contentService = contentService;
        this.drupalHttpClient = drupalHttpClient;
    }

    /**
     * Start crawling will do GET requests to all drupalUrls entries, then extract links from current step content and add them into drupalUrls list
     * Until no more drupalUrls are found
     */
    public void startCrawling() {
        log.info("Enter startCrawling method.");

        processFinished = false;
        Map<String, String> currentStepContent = new HashMap<>();
        List<String> urlsVisitedInCurrentStep = new ArrayList<>();
        try {
            do {
                drupalUrls.stream().forEach(url -> {
                    String responseBody = drupalHttpClient.getContent(url);
                    if (responseBody != null) {
                        currentStepContent.put(url, responseBody);
                    }
                    urlsVisitedInCurrentStep.add(url);
                });

                drupalUrls.removeAll(urlsVisitedInCurrentStep);

                currentStepContent.forEach((url, content) -> {
                    drupalUrls.addAll(contentService.collectLinksFromDrupalContent(url, content));
                    visitedUrls.put(url, content);
                    log.info("Parsing content: {}", content);
                    topLevelJsonapiMap.putAll(contentService.getTopLevelJsonapiDataMap());
                });

                drupalUrls.removeIf(drupalUrl -> visitedUrls.containsKey(drupalUrl));

                urlsVisitedInCurrentStep.clear();
                currentStepContent.clear();

            } while (!drupalUrls.isEmpty());

        } catch (ServiceException e) {
            throw new ServiceException("There was an error in the crawling method...", e);
        }

        processFinished = true;

        log.info("Crawling process is finished.");
    }

    /**
     * Return if the crawling process is done or not
     *
     * @return processFinished
     */
    public boolean isProcessFinished() {
        return processFinished;
    }

    /**
     * Return the visitedUrls map if the process is finished, else null
     *
     * @return
     */
    public Map<String, String> getVisitedUrls() {
        if (processFinished) {
            return visitedUrls;
        } else {
            return null;
        }
    }

}
