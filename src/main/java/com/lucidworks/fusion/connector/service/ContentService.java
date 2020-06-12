package com.lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.lucidworks.fusion.connector.content.DrupalContent;
import com.lucidworks.fusion.connector.content.DrupalContentEntry;
import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
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

    public DrupalContent getDrupalContent(String customUrl, DrupalLoginResponse drupalLoginResponse) {
        ImmutableMap.Builder<String, DrupalContentEntry> builder = ImmutableMap.builder();

        ResponseBody responseBody = drupalOkHttp.getDrupalContent(customUrl, drupalLoginResponse);

        try {
            DrupalContentEntry drupalContentEntry = new DrupalContentEntry(customUrl, responseBody.string(), ZonedDateTime.now().toEpochSecond());
            builder.put(customUrl, drupalContentEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DrupalContent(builder.build());

    }

    public void extractJsonFromDrupal(String url, DrupalLoginResponse drupalLoginResponse) {

        Map<String, TopLevelJsonapi> jsonapiMap = new HashMap<>();
        TopLevelJsonapi topLevelJsonapi = getTopLevelJsonContent(url + "en/fusion", drupalLoginResponse);
        jsonapiMap.put(url, topLevelJsonapi);

        List<LinkHref> list = new ArrayList<>(topLevelJsonapi.getLinks().values());

        jsonapiMap = mapDrupalContentToObject(list, jsonapiMap, drupalLoginResponse);

        //TODO add recursion for all links

        //return
    }

    private TopLevelJsonapi getTopLevelJsonContent(String url, DrupalLoginResponse drupalLoginResponse) {
        ResponseBody responseBody = drupalOkHttp.getDrupalContent(url, drupalLoginResponse);
        TopLevelJsonapi topLevelJsonapi = new TopLevelJsonapi();

        try {
            DrupalContentEntry drupalContentEntry = new DrupalContentEntry(url, responseBody.string(), ZonedDateTime.now().toEpochSecond());
            topLevelJsonapi = mapper.readValue(drupalContentEntry.getContent(), TopLevelJsonapi.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return topLevelJsonapi;
    }

    private Map<String, TopLevelJsonapi> mapDrupalContentToObject(List<LinkHref> list, Map<String, TopLevelJsonapi> jsonapiMap, DrupalLoginResponse drupalLoginResponse) {

        for (LinkHref linkHref : list) {
            ResponseBody body = drupalOkHttp.getDrupalContent(linkHref.getHref(), drupalLoginResponse);
            try {
                DrupalContentEntry drupalContentEntry = new DrupalContentEntry(linkHref.getHref(), body.string(), ZonedDateTime.now().toEpochSecond());
                TopLevelJsonapi topLevelJsonapi = mapper.readValue(drupalContentEntry.getContent(), TopLevelJsonapi.class);
                jsonapiMap.put(linkHref.getHref(), topLevelJsonapi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return jsonapiMap;

    }

    public DrupalLoginResponse login(String url, String username, String password) {
        DrupalLoginRequest drupalLoginRequest = new DrupalLoginRequest(username, password);

        ResponseBody loginResponse = drupalOkHttp.login(url, drupalLoginRequest);

        DrupalLoginResponse drupalLoginResponse = null;
        try {
            drupalLoginResponse = mapper.readValue(loginResponse.string(), DrupalLoginResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return drupalLoginResponse;
    }
}
