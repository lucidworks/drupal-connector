package com.lucidworks.fusion.connector.util;

import com.lucidworks.fusion.connector.model.Data;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;

import java.util.HashMap;
import java.util.Map;

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
            Map<String, Object> objectMap = new HashMap<>();
            TopLevelJsonapi topLevelJsonapi = topLevelJsonapiMap.get(url);

            if (topLevelJsonapi.getData() != null) {
                int i = 0;
                for (Data data : topLevelJsonapi.getData()) {
                    Map<String, Object> dataMap = prepareDataMap(data, ++i);
                    objectMap.putAll(dataMap);
                }

                objectMap.put("links", DataUtil.getLinks(topLevelJsonapi));
                objectMap.put("meta", DataUtil.getMeta(topLevelJsonapi));
                objectMap.put("errors", DataUtil.getErrors(topLevelJsonapi));
                objectMap.put("included", DataUtil.getIncluded(topLevelJsonapi));

                allObjectsMap.put(url, objectMap);
            }
        }

        return allObjectsMap;
    }

    private static Map<String, Object> prepareDataMap(Data data, int i) {
        Map<String, Object> dataMap = new HashMap<>();

        if (!DataUtil.getDataId(data).isEmpty()) {
            dataMap.put("data.id." + i, DataUtil.getDataId(data));
        }

        if (!DataUtil.getDataType(data).isEmpty()) {
            dataMap.put("data.type." + i, DataUtil.getDataType(data));
        }

        if (!DataUtil.getDataLinks(data).isEmpty()) {
            dataMap.put("data.links." + i, DataUtil.getDataLinks(data));
        }

        if (!DataUtil.getDataAttributeDrupalInternalNid(data).isEmpty()) {
            dataMap.put("attribute.drupal.internal.nid." + i, DataUtil.getDataAttributeDrupalInternalNid(data));
        }

        if (!DataUtil.getDataAttributeDrupalInternalVid(data).isEmpty()) {
            dataMap.put("attribute.drupal.internal.vid." + i, DataUtil.getDataAttributeDrupalInternalVid(data));
        }

        if (!DataUtil.getDataAttributeLangcode(data).isEmpty()) {
            dataMap.put("attribute.langcode." + i, DataUtil.getDataAttributeLangcode(data));
        }

        if (!DataUtil.getDataAttributeRevisionTimestamp(data).isEmpty()) {
            dataMap.put("attribute.revision.timestamp." + i, DataUtil.getDataAttributeRevisionTimestamp(data));
        }

        if (!DataUtil.getDataAttributeRevisionLog(data).isEmpty()) {
            dataMap.put("attribute.revision.log." + i, DataUtil.getDataAttributeRevisionLog(data));
        }

        dataMap.put("attribute.status." + i, DataUtil.getDataAttributeStatus(data));

        if (!DataUtil.getDataAttributeChanged(data).isEmpty()) {
            dataMap.put("attribute.changed." + i, DataUtil.getDataAttributeChanged(data));
        }

        dataMap.put("attribute.defaultLangcode." + i, DataUtil.getDataAttributeDefaultLangcode(data));
        dataMap.put("attribute.revision.translation.affected." + i, DataUtil.getDataAttributeRevisionTranslationAffected(data));

        if (!DataUtil.getDataAttributeTitle(data).isEmpty()) {
            dataMap.put("attribute.title." + i, DataUtil.getDataAttributeTitle(data));
        }

        if (!DataUtil.getDataAttributePath(data).isEmpty()) {
            dataMap.put("attribute.path." + i, DataUtil.getDataAttributePath(data));
        }

        if (data.getAttributes().getBody() != null) {
            dataMap.putAll(DataUtil.getDataAttributeBody(data, i));
        }

        if (!data.getAttributes().getFields().isEmpty()) {
            dataMap.putAll(DataUtil.getDataAttributeFields(data, i));
        }

        if (!data.getRelationships().getFields().isEmpty()) {
            dataMap.putAll(DataUtil.getDataRelationships(data, i));
        }

        if (!DataUtil.getDataHtmlLink(data).isEmpty()) {
            dataMap.put("html.link." + i, DataUtil.getDataHtmlLink(data));
        }

        return dataMap;
    }

    public static String getDataId(Data data) {
        return data.getId() != null ? data.getId() : "";
    }

    public static String getDataType(Data data) {
        return data.getType() != null ? data.getType() : "";
    }

    public static String getDataLinks(Data data) {
        return data.getLinks() != null ? data.getLinks().toString() : "";
    }

    public static String getDataAttributeDrupalInternalNid(Data data) {
        return data.getAttributes().getDrupalInternalNid() != null ? String.valueOf(data.getAttributes().getDrupalInternalNid()) : "";
    }

    public static String getDataAttributeDrupalInternalVid(Data data) {
        return data.getAttributes().getDrupalInternalVid() != null ? String.valueOf(data.getAttributes().getDrupalInternalVid()) : "";
    }

    public static String getDataAttributeLangcode(Data data) {
        return data.getAttributes().getLangcode() != null ? data.getAttributes().getLangcode() : "";
    }

    public static String getDataAttributeRevisionTimestamp(Data data) {
        return data.getAttributes().getRevisionCreated() != null ? data.getAttributes().getRevisionCreated() : "";
    }

    public static String getDataAttributeRevisionLog(Data data) {
        return data.getAttributes().getRevisionLogMessage() != null ? data.getAttributes().getRevisionLogMessage() : "";
    }

    public static boolean getDataAttributeStatus(Data data) {
        return data.getAttributes().isStatus();
    }

    public static String getDataAttributeChanged(Data data) {
        return data.getAttributes().getChanged() != null ? data.getAttributes().getChanged() : "";
    }

    public static boolean getDataAttributeDefaultLangcode(Data data) {
        return data.getAttributes().isDefaultLangcode();
    }

    public static boolean getDataAttributeRevisionTranslationAffected(Data data) {
        return data.getAttributes().isRevisionTranslationAffected();
    }

    public static String getDataAttributeTitle(Data data) {
        return data.getAttributes().getTitle() != null ? data.getAttributes().getTitle() : "";
    }

    public static String getDataAttributePath(Data data) {
        return data.getAttributes().getPath() != null ? data.getAttributes().getPath().toString() : "";
    }

    public static Map<String, Object> getDataAttributeBody(Data data, int i) {
        Map<String, Object> bodyFields = new HashMap<>();

        if (data.getAttributes().getBody().getValue() != null) {
            bodyFields.put("body.value." + i, data.getAttributes().getBody().getValue());
        }

        if (data.getAttributes().getBody().getFormat() != null) {
            bodyFields.put("body.format." + i, data.getAttributes().getBody().getFormat());
        }

        if (data.getAttributes().getBody().getProcessed() != null) {
            bodyFields.put("body.processed." + i, data.getAttributes().getBody().getProcessed());
        }

        if (data.getAttributes().getBody().getSummary() != null) {
            bodyFields.put("body.summary." + i, data.getAttributes().getBody().getSummary());
        }

        return bodyFields;
    }

    public static Map<String, Object> getDataAttributeFields(Data data, int i) {
        Map<String, Object> fieldsMap = new HashMap<>();

        for (String key : data.getAttributes().getFields().keySet()) {
            fieldsMap.put(key + "." + i, data.getAttributes().getFields().get(key));
        }

        return fieldsMap;
    }

    public static Map<String, Object> getDataRelationships(Data data, int i) {
        Map<String, Object> fieldsMap = new HashMap<>();

        for (String key : data.getRelationships().getFields().keySet()) {
            fieldsMap.put(key + "." + i, data.getRelationships().getFields().get(key));
        }

        return fieldsMap;
    }

    public static String getLinks(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getLinks() != null ? topLevelJsonapi.getLinks().toString() : "";
    }

    public static String getMeta(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getMeta() != null ? topLevelJsonapi.getMeta().toString() : "";
    }

    public static String getErrors(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getErrors() != null ? topLevelJsonapi.getErrors().toString() : "";
    }

    public static String getIncluded(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getIncluded() != null ? topLevelJsonapi.getIncluded().toString() : "";
    }

    public static String getDataHtmlLink(Data data) {
        String htmlLink = "";

        if (data.getLinks() != null && data.getLinks().size() > 0) {
            htmlLink = data.getLinks().get("html") != null ? data.getLinks().get("html").getHref() : "";
        }

        return htmlLink;
    }
}
