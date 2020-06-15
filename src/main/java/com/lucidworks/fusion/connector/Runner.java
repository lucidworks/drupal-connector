package com.lucidworks.fusion.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalOkHttp;

import java.util.Map;

public class Runner {

    public static void main(String[] args) {
        String baseUrl = "http://s5ece25faf2e8c4kc8tnpvvh.devcloud.acquia-sites.com/";
        DrupalOkHttp drupalOkHttp = new DrupalOkHttp();
        ObjectMapper mapper = new ObjectMapper();
        ContentService contentService = new ContentService(drupalOkHttp, mapper);

        DrupalLoginResponse drupalLoginResponse = contentService.login(baseUrl, "authenticated", "authenticated");

        ConnectorService connectorService = new ConnectorService(baseUrl + "en/fusion", drupalLoginResponse, contentService);

        Map<String, String> response = connectorService.prepareDataToUpload();

        response.forEach((currentUrl, content) -> {
            System.out.println(currentUrl);
            System.out.println(content);
        });
    }
}
