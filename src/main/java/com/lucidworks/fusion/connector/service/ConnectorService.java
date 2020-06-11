package com.lucidworks.fusion.connector.service;

import okhttp3.ResponseBody;

import java.util.Map;

public class ConnectorService {

    private final DrupalContentFetcher drupalContentFetcher;
    private boolean isProcessStarted = false;

    public ConnectorService(String drupalUrl) {
        this.drupalContentFetcher = new DrupalContentFetcher(drupalUrl);
    }

    public Map<String, ResponseBody> prepareDataToUpload() {
        Map<String, ResponseBody> dataToUpload;

        if (!isProcessStarted || !drupalContentFetcher.isProcessFinished()) {
            drupalContentFetcher.startFetch();
            isProcessStarted = true;
        }

        while(!drupalContentFetcher.isProcessFinished()) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return prepareDataToUpload();
            }
        }

        dataToUpload = drupalContentFetcher.getVisitedUrls();

        return dataToUpload;
    }

}
