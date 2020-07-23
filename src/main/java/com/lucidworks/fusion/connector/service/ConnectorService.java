package com.lucidworks.fusion.connector.service;

import com.lucidworks.fusion.connector.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Connector Service resolve the data for Fusion Fetcher
 */
@Slf4j
public class ConnectorService {

    private final DrupalContentCrawler drupalContentCrawler;
    private boolean isProcessStarted = false;

    public ConnectorService(String drupalUrl, ContentService contentService, DrupalHttpClient drupalHttpClient) {
        this.drupalContentCrawler = new DrupalContentCrawler(drupalUrl, contentService, drupalHttpClient);
    }

    /**
     * Prepare data to upload will wait until the crawling process is done and then return the result of it
     *
     * @return dataToUpload
     */
    public Map<String, String> prepareDataToUpload() {
        log.info("Method prepareDataToUpload...");

        Map<String, String> dataToUpload;

        try {
            if (!isProcessStarted || !drupalContentCrawler.isProcessFinished()) {
                drupalContentCrawler.startCrawling();
                isProcessStarted = true;
            }
        } catch (ServiceException e) {
            throw new ServiceException("There was an error on the crawling process!", e);
        }

        while (!drupalContentCrawler.isProcessFinished()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("Retrying to call the crawling method...");
                return prepareDataToUpload();
            }
        }

        log.info("Crawling process is finished.");
        dataToUpload = drupalContentCrawler.getVisitedUrls();

        return dataToUpload;
    }

}
