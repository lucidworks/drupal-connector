package com.lucidworks.fusion.connector.util;

import com.lucidworks.fusion.connector.model.Data;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class to extract the content of every field from a Drupal page.
 */
public final class DataUtil {

    private DataUtil() {
    }

    /**
     * Generate all the content from every page and add it to a map
     *
     * @param topLevelJsonapiMap
     * @return The map with all the content indexed
     */
    public static Map<String, Map<String, Object>> generateObjectMap(Map<String, TopLevelJsonapi> topLevelJsonapiMap) {
        Map<String, Map<String, Object>> allObjectsMap = new HashMap<>();

        for (String url : topLevelJsonapiMap.keySet()) {
            TopLevelJsonapi topLevelJsonapi = topLevelJsonapiMap.get(url);

            if (topLevelJsonapi.getData() != null) {
                for (Data data : topLevelJsonapi.getData()) {
                    Map<String, Object> dataMap = prepareDataMap(data);
                    dataMap = prepareRelationshipFields(dataMap, topLevelJsonapiMap, data);
                    if (dataMap.get("html.link") != null) {
                        allObjectsMap.put(dataMap.get("html.link").toString(), dataMap);
                    }
                }
            }
        }

        return allObjectsMap;
    }

    private static Map<String, Object> prepareRelationshipFields(Map<String, Object> dataMap, Map<String, TopLevelJsonapi> topLevelJsonapiMap, Data currentData) {

        String tagsUrl = getRelatedUrl(currentData, "tags");
        String categoryUrl = getRelatedUrl(currentData, "category");
        String imageUrl = getRelatedUrl(currentData, "image");

        if (!tagsUrl.isEmpty() && topLevelJsonapiMap.get(tagsUrl) != null && topLevelJsonapiMap.get(tagsUrl).getData() != null) {

            List<String> tags = new ArrayList<>();

            for (Data data : topLevelJsonapiMap.get(tagsUrl).getData()) {
                tags.add(data.getAttributes().getName());
            }

            if (!tags.isEmpty()) {
                dataMap.put("tags", tags);
            }
        }

        if (!categoryUrl.isEmpty() && topLevelJsonapiMap.get(categoryUrl) != null && topLevelJsonapiMap.get(categoryUrl).getData() != null) {
            List<String> category = new ArrayList<>();

            for (Data data : topLevelJsonapiMap.get(categoryUrl).getData()) {
                category.add(data.getAttributes().getName());
            }

            if (!category.isEmpty()) {
                dataMap.put("category", category);
            }
        }

        if (!imageUrl.isEmpty() && topLevelJsonapiMap.get(imageUrl) != null && topLevelJsonapiMap.get(imageUrl).getData() != null) {
            List<String> images = new ArrayList<>();

            for (Data data : topLevelJsonapiMap.get(imageUrl).getData()) {

                String image2Url = getRelatedUrl(data, "image");

                if (image2Url != null && topLevelJsonapiMap.get(image2Url) != null && topLevelJsonapiMap.get(image2Url).getData() != null) {
                    Data[] currentDataList = topLevelJsonapiMap.get(image2Url).getData();
                    for (Data currentStepData : currentDataList) {
                        if (currentStepData.getAttributes().getUri() != null) {
                            images.add(currentStepData.getAttributes().getUri().getUrl());
                        }
                    }
                } else if (data.getAttributes().getUri()!= null){
                    images.add(data.getAttributes().getUri().getUrl());
                }
            }

            if (!images.isEmpty()) {
                dataMap.put("image", images);
            }
        }

        return dataMap;
    }

    private static String getRelatedUrl(Data data, String requiredValue) {
        String url = "";

        if (data.getRelationships() != null) {
            for (String keySet : data.getRelationships().getFields().keySet()) {
                if (keySet.contains(requiredValue)) {
                    if (data.getRelationships().getFields().get(keySet).getLinks().get("related") != null) {
                        url = data.getRelationships().getFields().get(keySet).getLinks().get("related").getHref();
                    }
                }
            }
        }

        return url;
    }

    private static Map<String, Object> prepareDataMap(Data data) {
        Map<String, Object> dataMap = new HashMap<>();

        if (!DataUtil.getDataId(data).isEmpty()) {
            dataMap.put("id", DataUtil.getDataId(data));
        }

        if (!DataUtil.getDataType(data).isEmpty()) {
            dataMap.put("type", DataUtil.getDataType(data));
        }

        if (!DataUtil.getDataLinks(data).isEmpty()) {
            dataMap.put("links", DataUtil.getDataLinks(data));
        }

        if (!DataUtil.getDataAttributeDrupalInternalNid(data).isEmpty()) {
            dataMap.put("drupal.internal.nid", DataUtil.getDataAttributeDrupalInternalNid(data));
        }

        if (!DataUtil.getDataAttributeDrupalInternalVid(data).isEmpty()) {
            dataMap.put("drupal.internal.vid", DataUtil.getDataAttributeDrupalInternalVid(data));
        }

        if (!DataUtil.getDataAttributeLangcode(data).isEmpty()) {
            dataMap.put("langcode", DataUtil.getDataAttributeLangcode(data));
        }

        if (!DataUtil.getDataAttributeRevisionTimestamp(data).isEmpty()) {
            dataMap.put("revision.timestamp", DataUtil.getDataAttributeRevisionTimestamp(data));
        }

        if (!DataUtil.getDataAttributeRevisionLog(data).isEmpty()) {
            dataMap.put("revision.log", DataUtil.getDataAttributeRevisionLog(data));
        }

        dataMap.put("status", DataUtil.getDataAttributeStatus(data));

        if (!DataUtil.getDataAttributeChanged(data).isEmpty()) {
            dataMap.put("changed", DataUtil.getDataAttributeChanged(data));
        }

        dataMap.put("defaultLangcode", DataUtil.getDataAttributeDefaultLangcode(data));
        dataMap.put("revision.translation.affected", DataUtil.getDataAttributeRevisionTranslationAffected(data));

        if (!DataUtil.getDataAttributeTitle(data).isEmpty()) {
            dataMap.put("title", DataUtil.getDataAttributeTitle(data));
        }

        if (!DataUtil.getDataAttributePath(data).isEmpty()) {
            dataMap.put("path", DataUtil.getDataAttributePath(data));
        }

        if (data.getAttributes().getBody() != null) {
            dataMap.putAll(DataUtil.getDataAttributeBody(data));
        }

        if (!data.getAttributes().getFields().isEmpty()) {
            dataMap.putAll(DataUtil.getDataAttributeFields(data));
        }

        if (!data.getRelationships().getFields().isEmpty()) {
            dataMap.putAll(DataUtil.getDataRelationships(data));
        }

        if (!DataUtil.getDataHtmlLink(data).isEmpty()) {
            dataMap.put("html.link", DataUtil.getDataHtmlLink(data));
        }

        return dataMap;
    }

    private static String getDataId(Data data) {
        return data.getId() != null ? data.getId() : "";
    }

    private static String getDataType(Data data) {
        return data.getType() != null ? data.getType() : "";
    }

    private static String getDataLinks(Data data) {
        return data.getLinks() != null ? data.getLinks().toString() : "";
    }

    private static String getDataAttributeDrupalInternalNid(Data data) {
        return data.getAttributes().getDrupalInternalNid() != null ? String.valueOf(data.getAttributes().getDrupalInternalNid()) : "";
    }

    private static String getDataAttributeDrupalInternalVid(Data data) {
        return data.getAttributes().getDrupalInternalVid() != null ? String.valueOf(data.getAttributes().getDrupalInternalVid()) : "";
    }

    private static String getDataAttributeLangcode(Data data) {
        return data.getAttributes().getLangcode() != null ? data.getAttributes().getLangcode() : "";
    }

    private static String getDataAttributeRevisionTimestamp(Data data) {
        return data.getAttributes().getRevisionCreated() != null ? data.getAttributes().getRevisionCreated() : "";
    }

    private static String getDataAttributeRevisionLog(Data data) {
        return data.getAttributes().getRevisionLogMessage() != null ? data.getAttributes().getRevisionLogMessage() : "";
    }

    private static boolean getDataAttributeStatus(Data data) {
        return data.getAttributes().isStatus();
    }

    private static String getDataAttributeChanged(Data data) {
        return data.getAttributes().getChanged() != null ? data.getAttributes().getChanged() : "";
    }

    private static boolean getDataAttributeDefaultLangcode(Data data) {
        return data.getAttributes().isDefaultLangcode();
    }

    private static boolean getDataAttributeRevisionTranslationAffected(Data data) {
        return data.getAttributes().isRevisionTranslationAffected();
    }

    private static String getDataAttributeTitle(Data data) {
        return data.getAttributes().getTitle() != null ? data.getAttributes().getTitle() : "";
    }

    private static String getDataAttributePath(Data data) {
        return data.getAttributes().getPath() != null ? data.getAttributes().getPath().toString() : "";
    }

    private static Map<String, Object> getDataAttributeBody(Data data) {
        Map<String, Object> bodyFields = new HashMap<>();

        if (data.getAttributes().getBody().getValue() != null) {
            bodyFields.put("body.value", data.getAttributes().getBody().getValue());
        }

        if (data.getAttributes().getBody().getFormat() != null) {
            bodyFields.put("body.format", data.getAttributes().getBody().getFormat());
        }

        if (data.getAttributes().getBody().getProcessed() != null) {
            bodyFields.put("body.processed", data.getAttributes().getBody().getProcessed());
        }

        if (data.getAttributes().getBody().getSummary() != null) {
            bodyFields.put("body.summary", data.getAttributes().getBody().getSummary());
        }

        return bodyFields;
    }

    private static Map<String, Object> getDataAttributeFields(Data data) {
        Map<String, Object> fieldsMap = new HashMap<>();

        if (data.getAttributes() != null && data.getAttributes().getFields() != null) {
            for (String key : data.getAttributes().getFields().keySet()) {
                if (data.getAttributes().getFields().get(key) != null) {
                    Object fieldObject = data.getAttributes().getFields().get(key);
                    if (fieldObject instanceof Map<?,?>) {
                        Map<String, Object> fieldObjectMap = Map.class.cast(fieldObject);
                        for (String fieldObjectKey : fieldObjectMap.keySet()) {
                            fieldsMap.put(key + "_" + fieldObjectKey, fieldObjectMap.get(fieldObjectKey));
                        }
                    } else {
                        fieldsMap.put(key, data.getAttributes().getFields().get(key));
                    }
                }
            }
        }


        return fieldsMap;
    }

    private static Map<String, Object> getDataRelationships(Data data) {
        Map<String, Object> fieldsMap = new HashMap<>();

        for (String key : data.getRelationships().getFields().keySet()) {
            fieldsMap.put(key, data.getRelationships().getFields().get(key));
        }

        return fieldsMap;
    }

    private static String getDataHtmlLink(Data data) {
        String htmlLink = "";

        if (data.getLinks() != null && data.getLinks().size() > 0) {
            htmlLink = data.getLinks().get("html") != null ? data.getLinks().get("html").getHref() : "";
        }

        return htmlLink;
    }
}
