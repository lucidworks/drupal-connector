package com.lucidworks.fusion.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalHttpClient;
import com.lucidworks.fusion.connector.util.DataUtil;

import java.util.Map;

public class Runner {

    public static void main(String[] args) {
        String baseUrl = "http://s5efe1a8b62a65rx9apyfzmk.devcloud.acquia-sites.com";
        ObjectMapper mapper = new ObjectMapper();

        DrupalHttpClient drupalHttpClient = new DrupalHttpClient();

        String loginResponse = drupalHttpClient.doLogin("http://s5efe1a8b62a65rx9apyfzmk.devcloud.acquia-sites.com/user/login", "admin", "Admin@Fusion");

        ContentService contentService = new ContentService(mapper);

        ConnectorService connectorService = new ConnectorService(normalizeUrl(baseUrl) + normalizeUrl("/en/fusion"), contentService, drupalHttpClient);

        Map<String, String> response = connectorService.prepareDataToUpload();

        Map<String, TopLevelJsonapi> topLevelJsonapiMap = contentService.getTopLevelJsonapiDataMap();

        Map<String, Map<String, Object>> objectMap = DataUtil.generateObjectMap(topLevelJsonapiMap);

        for (String key : objectMap.keySet()) {
            Map<String, Object> pageContentMap = objectMap.get(key);
            System.out.println("Key: " + key);
        }

    }

    private static String normalizeUrl(String initialUrl) {
        String normalizedUrl = initialUrl.endsWith("/") ?
                initialUrl.substring(0, initialUrl.length() - 1) : initialUrl;

        return normalizedUrl;
    }
}
