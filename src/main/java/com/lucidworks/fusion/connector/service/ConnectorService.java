package com.lucidworks.fusion.connector.service;

import com.lucidworks.fusion.connector.model.DrupalLoginResponse;

import java.util.Map;

/**
 * Connector Service resolve the data for Fusion Fetcher
 */
public class ConnectorService {

    private final DrupalContentCrawler drupalContentCrawler;
    private boolean isProcessStarted = false;

    public ConnectorService(String drupalUrl, DrupalLoginResponse drupalLoginResponse) {
        this.drupalContentCrawler = new DrupalContentCrawler(drupalUrl, drupalLoginResponse);
    }

    /**
     * Prepare data to upload will wait until the crawling process is done and then return the result of it
     *
     * @return dataToUpload
     */
    public Map<String, String> prepareDataToUpload() {
        Map<String, String> dataToUpload;

        if (!isProcessStarted || !drupalContentCrawler.isProcessFinished()) {
            drupalContentCrawler.startCrawling();
            isProcessStarted = true;
        }

        while (!drupalContentCrawler.isProcessFinished()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return prepareDataToUpload();
            }
        }

        dataToUpload = drupalContentCrawler.getVisitedUrls();

        return dataToUpload;
    }

}
