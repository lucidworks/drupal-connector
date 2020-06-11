package com.lucidworks.fusion.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalOkHttp;
import okhttp3.ResponseBody;

import java.util.Map;

public class Runner {

    public static void main(String[] args) {
        String url = "http://s5ece25faf2e8c4kc8tnpvvh.devcloud.acquia-sites.com/fusion";

        DrupalOkHttp drupalOkHttp = new DrupalOkHttp();
        ObjectMapper mapper = new ObjectMapper();
        ContentService contentService = new ContentService(drupalOkHttp, mapper);

        contentService.extractJsonFromDrupal(url);

        ConnectorService connectorService = new ConnectorService(url);

        Map<String, ResponseBody> response = connectorService.prepareDataToUpload();

        response.forEach((currentUrl, content) -> {
            System.out.println(currentUrl);
        });
        //System.out.println(topLevelJsonapi.toString());
    }
}
