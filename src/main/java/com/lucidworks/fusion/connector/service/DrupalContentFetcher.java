package com.lucidworks.fusion.connector.service;

import okhttp3.ResponseBody;

import java.util.*;

public class DrupalContentFetcher {

    private boolean processFinished = false;
    private List<String> drupalUrls;
    private Map<String, ResponseBody> visitedUrls;

    private DrupalOkHttp drupalOkHttp;

    public DrupalContentFetcher(String drupalUrl) {
        drupalOkHttp = new DrupalOkHttp();
        drupalUrls = Arrays.asList(drupalUrl);
    }

    public void startFetch() {
        Map<String, ResponseBody> currentStepContent = new HashMap<>();
        List<String> urlsVisitedInCurrentStep = new ArrayList<>();
        do {
            drupalUrls.stream().forEach(url -> {
                currentStepContent.put(url, drupalOkHttp.getDrupalContent(url));
                urlsVisitedInCurrentStep.add(url);
            });

            drupalUrls.remove(urlsVisitedInCurrentStep);

            currentStepContent.forEach((url, content) -> {
                drupalUrls.addAll(extractLinkFromContent(content));
                visitedUrls.put(url, content);
            });

            urlsVisitedInCurrentStep.clear();
            currentStepContent.clear();

        } while (!drupalUrls.isEmpty());

        processFinished = true;
    }

    public boolean isProcessFinished() {
        return processFinished;
    }

    public Map<String, ResponseBody> getVisitedUrls() {
        if (processFinished) {
            return visitedUrls;
        } else {
            return null;
        }
    }

    public List<String> extractLinkFromContent(ResponseBody content) {
        List<String> urls = new ArrayList<>();

        //Integrate with Cristina's code

        return urls;
    }



}
