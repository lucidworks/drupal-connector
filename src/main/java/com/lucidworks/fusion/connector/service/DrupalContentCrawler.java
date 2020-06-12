package com.lucidworks.fusion.connector.service;

import com.lucidworks.fusion.connector.model.DrupalLoginResponse;

import java.io.IOException;
import java.util.*;

/**
 * Drupal Content Crawler can create a Map with all links and content from them.
 */
public class DrupalContentCrawler {

    private boolean processFinished = false;
    private List<String> drupalUrls;
    private Map<String, String> visitedUrls;
    private DrupalLoginResponse loggedInUser;
    private DrupalOkHttp drupalOkHttp;
    private ContentService contentService;

    /**
     * Constructor for Crawler
     *
     * @param drupalUrl    the url for the first GET request
     * @param loggedInUser the loggedInUser with JWT token inside
     */
    public DrupalContentCrawler(String drupalUrl, DrupalLoginResponse loggedInUser, ContentService contentService) {
        this.drupalOkHttp = new DrupalOkHttp();
        this.loggedInUser = loggedInUser;

        this.drupalUrls = new ArrayList<>(Arrays.asList(drupalUrl));
        this.visitedUrls = new HashMap<>();

        this.contentService = contentService;
    }

    /**
     * Start crawling will do GET requests to all drupalUrls entries, then extract links from current step content and add them into drupalUrls list
     * Until no more drupalUrls are found
     */
    public void startCrawling() {
        Map<String, String> currentStepContent = new HashMap<>();
        List<String> urlsVisitedInCurrentStep = new ArrayList<>();
        do {
            drupalUrls.stream().forEach(url -> {
                try {
                    currentStepContent.put(url, drupalOkHttp.getDrupalContent(url, loggedInUser).string());
                    urlsVisitedInCurrentStep.add(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            drupalUrls.removeAll(urlsVisitedInCurrentStep);

            currentStepContent.forEach((url, content) -> {
                drupalUrls.addAll(contentService.collectLinksFromDrupalContent(content));
                visitedUrls.put(url, content);
            });

            urlsVisitedInCurrentStep.clear();
            currentStepContent.clear();

        } while (!drupalUrls.isEmpty());

        processFinished = true;
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
