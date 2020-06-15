package com.lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.lucidworks.fusion.connector.content.DrupalContent;
import com.lucidworks.fusion.connector.content.DrupalContentEntry;
import com.lucidworks.fusion.connector.model.Data;
import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.model.RelationshipFields;
import com.lucidworks.fusion.connector.model.TopLevelJsonApiData;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Content Service fetch the content from Drupal
 */
public class ContentService {

    private final String SELF_LINK = "self";

    private final DrupalOkHttp drupalOkHttp;
    private final ObjectMapper mapper;

    @Inject
    public ContentService(DrupalOkHttp drupalOkHttp, ObjectMapper objectMapper) {
        this.drupalOkHttp = drupalOkHttp;
        this.mapper = objectMapper;
    }

    /**
     * @param customUrl           The url where the content is taken
     * @param drupalLoginResponse The current user with JWT token
     * @return
     */
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

    /**
     * Collect all the links inside a page
     *
     * @param content The entire content from a web page
     * @return List with all the links found
     */
    public List<String> collectLinksFromDrupalContent(String content) {

        List<String> links = new ArrayList<>();
        TopLevelJsonapi topLevelJsonapi = null;

        try {
            topLevelJsonapi = mapper.readValue(content, TopLevelJsonapi.class);

        } catch (IOException e) {
            try {
                topLevelJsonapi = mapper.readValue(content, TopLevelJsonApiData.class);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (topLevelJsonapi.getData() != null) {
            List<Data> dataList = Arrays.asList(topLevelJsonapi.getData());

            dataList.stream()
                    .filter(d -> d.getRelationships() != null)
                    //.parallel()
                    .forEach(data -> {
                        Collection<RelationshipFields> fields = data.getRelationships().getFields().values();
                        fields.forEach(f -> {
                            f.getLinks().forEach((k, v) -> {
                                if (!k.equals(SELF_LINK)) {
                                    links.add(v.getHref());
                                }
                            });
                        });
                    });
        }

        if (topLevelJsonapi.getLinks() != null || !topLevelJsonapi.getLinks().isEmpty()) {
            topLevelJsonapi.getLinks().forEach((k, v) -> {
                if (!k.equals(SELF_LINK)) {
                    links.add(v.getHref());
                }
            });
        }

        return links;
    }

    /**
     * Request to login the user in order to have the JWT token
     *
     * @param url
     * @param username
     * @param password
     * @return
     */
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
