package com.lucidworks.fusion.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.content.DrupalContent;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalOkHttp;

public class Runner {

    public static void main(String[] args) {
        String baseUrl = "http://s5ece25faf2e8c4kc8tnpvvh.devcloud.acquia-sites.com/";
        DrupalOkHttp drupalOkHttp = new DrupalOkHttp();
        ObjectMapper mapper = new ObjectMapper();
        ContentService contentService = new ContentService(drupalOkHttp, mapper);

        DrupalLoginResponse drupalLoginResponse = contentService.login(baseUrl, "authenticated", "authenticated");

        DrupalContent drupalContent = contentService.getDrupalContent(baseUrl, drupalLoginResponse);

        System.out.println(drupalContent.getEntries());
    }
}
