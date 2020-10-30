package com.lucidworks.fusion.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import com.lucidworks.fusion.connector.service.ConnectorService;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalHttpClient;
import com.lucidworks.fusion.connector.util.DataUtil;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Runner {

    public static void main(String[] args) {
        String baseUrl = "https://aws-drupal.appbase.io";
        ObjectMapper mapper = new ObjectMapper();

        DrupalHttpClient drupalHttpClient = new DrupalHttpClient();

        //String loginResponse = drupalHttpClient.doLogin("https://aws-drupal.appbase.io/user/login", "user", "e8cbDwNUHfeB");

        ContentService contentService = new ContentService(mapper);

        ConnectorService connectorService = new ConnectorService(normalizeUrl(baseUrl) + normalizeUrl("/fusion"), contentService, drupalHttpClient);

        Map<String, String> response = connectorService.prepareDataToUpload();

        Map<String, TopLevelJsonapi> topLevelJsonapiMap = contentService.getTopLevelJsonapiDataMap();

        Map<String, Map<String, Object>> objectMap = DataUtil.generateObjectMap(topLevelJsonapiMap);

        System.out.println(objectMap);

        for (String key : objectMap.keySet()) {
            Map<String, Object> pageContentMap = objectMap.get(key);
            List<String> keysToRemove = new ArrayList<>();
            for (String innerKey : pageContentMap.keySet()) {
                Object innerValue = pageContentMap.get(innerKey);
                if (!(innerValue instanceof Number || innerValue instanceof String ||
                        innerValue instanceof Boolean || innerValue instanceof Date)) {
                    System.out.println("Removing bad key/value: " + innerKey + " -> " + innerValue);
                    keysToRemove.add(innerKey);
                }
            }
            for (String keyToRemove : keysToRemove) {
                pageContentMap.remove(keyToRemove);
            }
        }

        for (String key : objectMap.keySet()) {
            Map<String, Object> pageContentMap = objectMap.get(key);
            MapUtils.debugPrint(System.out, null, objectMap);
        }

    }

    private static String normalizeUrl(String initialUrl) {
        String normalizedUrl = initialUrl.endsWith("/") ?
                initialUrl.substring(0, initialUrl.length() - 1) : initialUrl;

        return normalizedUrl;
    }
}
