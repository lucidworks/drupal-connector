package com.lucidworks.fusion.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.service.ContentService;
import com.lucidworks.fusion.connector.service.DrupalOkHttp;

import java.util.List;

public class Runner {

    public static void main(String[] args) {
        String url = "http://s5ece25faf2e8c4kc8tnpvvh.devcloud.acquia-sites.com/en/fusion/node/recipe";

        DrupalOkHttp drupalOkHttp = new DrupalOkHttp();
        ObjectMapper mapper = new ObjectMapper();
        ContentService contentService = new ContentService(drupalOkHttp, mapper);

        List<String> list = contentService.extractJsonFromDrupal(url);
        System.out.println(list.toString());
    }
}
