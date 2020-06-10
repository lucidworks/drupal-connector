package com.lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.lucidworks.fusion.connector.content.DrupalContent;
import com.lucidworks.fusion.connector.content.DrupalContentEntry;
import com.lucidworks.fusion.connector.model.LinkHref;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContentService {

    private final String URL = "http://s5ece25faf2e8c4kc8tnpvvh.devcloud.acquia-sites.com/fusion";

    private final DrupalOkHttp drupalOkHttp;
    private final ObjectMapper mapper;

    @Inject
    public ContentService(DrupalOkHttp drupalOkHttp, ObjectMapper objectMapper) {
        this.drupalOkHttp = drupalOkHttp;
        this.mapper = objectMapper;
    }

    public DrupalContent getDrupalContent(String customUrl) {
        ImmutableMap.Builder<String, DrupalContentEntry> builder = ImmutableMap.builder();

        String url = customUrl.isEmpty() ? URL : customUrl;
        ResponseBody responseBody = drupalOkHttp.getDrupalContent(url);

        try {
            DrupalContentEntry drupalContentEntry = new DrupalContentEntry(url, responseBody.string(), ZonedDateTime.now().toEpochSecond());
            builder.put(url, drupalContentEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DrupalContent(builder.build());

    }

    public void extractJsonFromDrupal(String url) {

        Map<String, TopLevelJsonapi> jsonapiMap = new HashMap<>();
        TopLevelJsonapi topLevelJsonapi = getTopLevelJsonContent(url);
        jsonapiMap.put(url, topLevelJsonapi);

        List<LinkHref> list = new ArrayList<>(topLevelJsonapi.getLinks().values());

        jsonapiMap = mapDrupalContentToObject(list, jsonapiMap);

        //TODO add recursion for all links

        //return
    }

    private TopLevelJsonapi getTopLevelJsonContent(String url) {
        ResponseBody responseBody = drupalOkHttp.getDrupalContent(url);
        TopLevelJsonapi topLevelJsonapi = new TopLevelJsonapi();

        try {
            DrupalContentEntry drupalContentEntry = new DrupalContentEntry(url, responseBody.string(), ZonedDateTime.now().toEpochSecond());
            topLevelJsonapi = mapper.readValue(drupalContentEntry.getContent(), TopLevelJsonapi.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return topLevelJsonapi;
    }

    private Map<String, TopLevelJsonapi> mapDrupalContentToObject(List<LinkHref> list, Map<String, TopLevelJsonapi> jsonapiMap) {

        list.stream().forEach(linkHref -> {
            ResponseBody body = drupalOkHttp.getDrupalContent(linkHref.getHref());
            try {
                DrupalContentEntry drupalContentEntry = new DrupalContentEntry(linkHref.getHref(), body.string(), ZonedDateTime.now().toEpochSecond());
                TopLevelJsonapi topLevelJsonapi = mapper.readValue(drupalContentEntry.getContent(), TopLevelJsonapi.class);
                jsonapiMap.put(linkHref.getHref(), topLevelJsonapi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return jsonapiMap;

    }

}
