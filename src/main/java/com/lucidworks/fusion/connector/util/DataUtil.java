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

    public static String getDataId(Data data) {
        return data.getId() != null ? data.getId() : "No id";
    }

    public static String getDataType(Data data) {
        return data.getType() != null ? data.getType() : "No type";
    }

    public static String getDataLinks(Data data) {
        return data.getLinks() != null ? data.getLinks().toString() : "No data links";
    }

    public static String getDataAttributeDrupalInternalNid(Data data) {
        return data.getAttributes().getDrupalInternalNid() != null ? String.valueOf(data.getAttributes().getDrupalInternalNid()) : "No drupalInternalNid";
    }

    public static String getDataAttributeDrupalInternalVid(Data data) {
        return data.getAttributes().getDrupalInternalVid() != null ? String.valueOf(data.getAttributes().getDrupalInternalVid()) : "No drupalInternalVid";
    }

    public static String getDataAttributeLangcode(Data data) {
        return data.getAttributes().getLangcode() != null ? data.getAttributes().getLangcode() : "No langcode";
    }

    public static String getDataAttributeRevisionTimestamp(Data data) {
        return data.getAttributes().getRevisionCreated() != null ? data.getAttributes().getRevisionCreated() : "No revisionTimestamp";
    }

    public static String getDataAttributeRevisionLog(Data data) {
        return data.getAttributes().getRevisionLogMessage() != null ? data.getAttributes().getRevisionLogMessage() : "No revisionLog";
    }

    public static boolean getDataAttributeStatus(Data data) {
        return data.getAttributes().isStatus();
    }

    public static String getDataAttributeChanged(Data data) {
        return data.getAttributes().getChanged() != null ? data.getAttributes().getChanged() : "No changed";
    }

    public static boolean getDataAttributeDefaultLangcode(Data data) {
        return data.getAttributes().isDefaultLangcode();
    }

    public static boolean getDataAttributeRevisionTranslationAffected(Data data) {
        return data.getAttributes().isRevisionTranslationAffected();
    }

    public static String getDataAttributeTitle(Data data) {
        return data.getAttributes().getTitle() != null ? data.getAttributes().getTitle() : "No title";
    }

    public static String getDataAttributePath(Data data) {
        return data.getAttributes().getPath() != null ? data.getAttributes().getPath().toString() : "No path content";
    }

    public static String getDataAttributeBody(Data data) {
        return data.getAttributes().getBody() != null ? data.getAttributes().getBody().getValue() : "No body";
    }

    public static String getDataAttributeFields(Data data) {
        return data.getAttributes().getFields() != null ? data.getAttributes().getFields().toString() : "No extra fields";
    }

    public static String getDataRelationships(Data data) {
        return data.getRelationships() != null ? data.getRelationships().getFields().toString() : "No relationships fields";
    }

    public static String getLinks(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getLinks() != null ? topLevelJsonapi.getLinks().toString() : "No links";
    }

    public static String getMeta(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getMeta() != null ? topLevelJsonapi.getMeta().toString() : "No meta";
    }

    public static String getErrors(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getErrors() != null ? topLevelJsonapi.getErrors().toString() : "No Errors";
    }

    public static String getIncluded(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getIncluded() != null ? topLevelJsonapi.getIncluded().toString() : "No extra info";
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
                int i = 1;
                for (Data data : topLevelJsonapi.getData()) {
                    Map<String, Object> dataMap = prepareDataMap(data);
                    objectMap.put("data_" + i++, dataMap.values());
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

    private static Map<String, Object> prepareDataMap(Data data) {
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("data_id", DataUtil.getDataId(data));
        dataMap.put("data_type", DataUtil.getDataType(data));
        dataMap.put("data_links", DataUtil.getDataLinks(data));

        dataMap.put("attribute_drupal_internal__nid", DataUtil.getDataAttributeDrupalInternalNid(data));
        dataMap.put("attribute_drupal_internal__vid", DataUtil.getDataAttributeDrupalInternalVid(data));
        dataMap.put("attribute_langcode", DataUtil.getDataAttributeLangcode(data));
        dataMap.put("attribute_revision_timestamp", DataUtil.getDataAttributeRevisionTimestamp(data));
        dataMap.put("attribute_revision_log", DataUtil.getDataAttributeRevisionLog(data));
        dataMap.put("attribute_status", DataUtil.getDataAttributeStatus(data));
        dataMap.put("attribute_changed", DataUtil.getDataAttributeChanged(data));
        dataMap.put("attribute_defaultLangcode", DataUtil.getDataAttributeDefaultLangcode(data));
        dataMap.put("attribute_revision_translation_affected", DataUtil.getDataAttributeRevisionTranslationAffected(data));
        dataMap.put("attribute_title", DataUtil.getDataAttributeTitle(data));
        dataMap.put("attribute_path", DataUtil.getDataAttributePath(data));
        dataMap.put("attribute_body_value", DataUtil.getDataAttributeBody(data));
        dataMap.put("attribute_fields", DataUtil.getDataAttributeFields(data));

        dataMap.put("relationships_fields", DataUtil.getDataRelationships(data));

        return dataMap;
    }
}
