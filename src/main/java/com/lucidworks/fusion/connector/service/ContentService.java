package com.lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucidworks.fusion.connector.exception.ServiceException;
import com.lucidworks.fusion.connector.model.Data;
import com.lucidworks.fusion.connector.model.DrupalLoginRequest;
import com.lucidworks.fusion.connector.model.DrupalLoginResponse;
import com.lucidworks.fusion.connector.model.RelationshipFields;
import com.lucidworks.fusion.connector.model.TopLevelJsonApiData;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Content Service fetch the content from Drupal
 */
@Slf4j
public class ContentService {

    private final String SELF_LINK = "self";

    private final DrupalOkHttp drupalOkHttp;
    private final ObjectMapper mapper;
    private Map<String, TopLevelJsonapi> topLevelJsonapiDataMap;

    @Inject
    public ContentService(DrupalOkHttp drupalOkHttp, ObjectMapper objectMapper) {
        this.drupalOkHttp = drupalOkHttp;
        this.mapper = objectMapper;

        topLevelJsonapiDataMap = new HashMap<>();
    }

    /**
     * @param customUrl           The url where the content is taken
     * @param drupalLoginResponse The current user with JWT token
     * @return
     */
//    public DrupalContent getDrupalContent(String customUrl, DrupalLoginResponse drupalLoginResponse) {
//        ImmutableMap.Builder<String, DrupalContentEntry> builder = ImmutableMap.builder();
//
//        ResponseBody responseBody = drupalOkHttp.getDrupalContent(customUrl, drupalLoginResponse);
//
//        try {
//            DrupalContentEntry drupalContentEntry = new DrupalContentEntry(customUrl, responseBody.string(), ZonedDateTime.now().toEpochSecond());
//            builder.put(customUrl, drupalContentEntry);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return new DrupalContent(builder.build());
//
//    }

    /**
     * Collect all the links inside a page
     *
     * @param content The entire content from a web page
     * @return List with all the links found
     */
    public List<String> collectLinksFromDrupalContent(String url, String content) {
        log.info("Enter collectLinksFromDrupalContent method...");

        List<String> links = new ArrayList<>();
        TopLevelJsonapi topLevelJsonapi = null;

        try {
            topLevelJsonapi = mapper.readValue(content, TopLevelJsonapi.class);

        } catch (IOException e) {
            try {
                topLevelJsonapi = mapper.readValue(content, TopLevelJsonApiData.class);
            } catch (IOException ex) {
                throw new ServiceException("The mapper was unable to read the content!", ex);
            }
        }

        if (topLevelJsonapi.getData() != null) {
            topLevelJsonapiDataMap.put(url, topLevelJsonapi);
            List<Data> dataList = Arrays.asList(topLevelJsonapi.getData());

            dataList.stream()
                    .filter(data -> data.getRelationships() != null)
                    //.parallel()
                    .forEach(data -> {
                        Collection<RelationshipFields> relationshipFields = data.getRelationships().getFields().values();
                        relationshipFields.forEach(fields -> {
                            fields.getLinks().forEach((linkTag, linkHref) -> {
                                if (!linkTag.equals(SELF_LINK)) {
                                    links.add(linkHref.getHref());
                                }
                            });
                        });
                    });
        }

        if (topLevelJsonapi.getLinks() != null || !topLevelJsonapi.getLinks().isEmpty()) {
            topLevelJsonapi.getLinks().forEach((linkTag, linkHref) -> {
                if (!linkTag.equals(SELF_LINK)) {
                    links.add(linkHref.getHref());
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
        log.info("Trying to login the user {}", username);

        DrupalLoginRequest drupalLoginRequest = new DrupalLoginRequest(username, password);

        ResponseBody loginResponse = drupalOkHttp.login(url, drupalLoginRequest);

        DrupalLoginResponse drupalLoginResponse = null;
        try {
            drupalLoginResponse = mapper.readValue(loginResponse.string(), DrupalLoginResponse.class);
        } catch (IOException e) {
            throw new ServiceException("Failed to get the loginResponse from login request.", e);
        }

        log.info("User: {} logged in", username);
        return drupalLoginResponse;
    }

}
