package com.lucidworks.fusion.connector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.lucidworks.fusion.connector.content.DrupalContent;
import com.lucidworks.fusion.connector.content.DrupalContentEntry;
import com.lucidworks.fusion.connector.model.Data;
import com.lucidworks.fusion.connector.model.RelationshipFields;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ContentService {

    private final String URL = "http://s5ece25faf2e8c4kc8tnpvvh.devcloud.acquia-sites.com/fusion";
    private final String LINK_SELF = "self";

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

    public List<String> extractJsonFromDrupal(String url) {

        TopLevelJsonapi topLevelJsonapi = getTopLevelJsonContent(url);
        List<String> list = collectLinksFromDrupalContent(topLevelJsonapi);

        return list;
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

    private List<String> collectLinksFromDrupalContent(TopLevelJsonapi topLevelJsonapi) {

        List<String> links = new ArrayList<>();

        if (topLevelJsonapi.getData() != null || topLevelJsonapi.getData().length > 0) {
            List<Data> dataList = Arrays.asList(topLevelJsonapi.getData());

            dataList.stream()
                    .filter(d -> d.getRelationships() != null)
                    //.parallel()
                    .forEach(data -> {
                        Collection<RelationshipFields> fields = data.getRelationships().getFields().values();
                        fields.forEach(f -> {
                            f.getLinks().forEach((k, v) -> {
                                if (!k.equals(LINK_SELF)) {
                                    links.add(v.getHref());
                                }
                            });
                        });
                    });
        }

        if (topLevelJsonapi.getLinks() != null) {
            topLevelJsonapi.getLinks().forEach((k, v) -> {
                if (!k.equals(LINK_SELF)) {
                    links.add(v.getHref());
                }
            });
        }

        return links;
    }

//    private Map<String, TopLevelJsonapi> mapDrupalContentToObject(List<LinkHref> list, Map<String, TopLevelJsonapi> jsonapiMap) {
//
//        list.stream().forEach(linkHref -> {
//            ResponseBody body = drupalOkHttp.getDrupalContent(linkHref.getHref());
//            try {
//                DrupalContentEntry drupalContentEntry = new DrupalContentEntry(linkHref.getHref(), body.string(), ZonedDateTime.now().toEpochSecond());
//                TopLevelJsonapi topLevelJsonapi = mapper.readValue(drupalContentEntry.getContent(), TopLevelJsonapi.class);
//                jsonapiMap.put(linkHref.getHref(), topLevelJsonapi);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//        return jsonapiMap;
//
//    }

}
