package com.lucidworks.fusion.connector.util;

import com.lucidworks.fusion.connector.model.Data;
import com.lucidworks.fusion.connector.model.TopLevelJsonapi;

import java.util.HashMap;
import java.util.Map;

public final class DataUtil {

    private DataUtil() {
    }

    public static String getDataId(Data data) {
        return data.getId().isEmpty() ? "No Id" : data.getId();
    }

    public static String getDataType(Data data) {
        return data.getType().isEmpty() ? "No Type" : data.getType();
    }

    public static String getDataLinks(Data data) {
        return data.getLinks().isEmpty() ? "No Data Links" : data.getLinks().toString();
    }

    public static String getDataAttributeDrupalInternalNid(Data data) {
        return data.getAttributes().getDrupalInternalNid() != null ? String.valueOf(data.getAttributes().getDrupalInternalNid()) : "No DrupalInternalNid";
    }

    public static String getDataAttributeDrupalInternalVid(Data data) {
        return data.getAttributes().getDrupalInternalVid() != null ? String.valueOf(data.getAttributes().getDrupalInternalVid()) : "No DrupalInternalVid";
    }

    public static String getDataAttributeLangcode(Data data) {
        return data.getAttributes().getLangcode() != null ? data.getAttributes().getLangcode() : "No Langcode";
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

    public static String getDataAttributePath(Data data) {
        return data.getAttributes().getPath() != null ? data.getAttributes().getPath().toString() : "No Path content.";
    }

    public static String getDataAttributeFields(Data data) {
        return data.getAttributes().getFields() != null ? data.getAttributes().getFields().toString() : "No Extra Fields";
    }

    public static String getDataRelationships(Data data) {
        return data.getRelationships() != null ? data.getRelationships().getFields().toString() : "No Relationships Fields";
    }

    public static String getLinks(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getLinks() != null ? topLevelJsonapi.getLinks().toString() : "No Links";
    }

    public static String getMeta(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getMeta() != null ? topLevelJsonapi.getMeta().toString() : "No Meta";
    }

    public static String getErrors(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getErrors() != null ? topLevelJsonapi.getErrors().toString() : "No Errors";
    }

    public static String getIncluded(TopLevelJsonapi topLevelJsonapi) {
        return topLevelJsonapi.getIncluded() != null ? topLevelJsonapi.getIncluded().toString() : "No Extra Info";
    }

    public static Map<String, Object> generateObjectMap(Map<String, TopLevelJsonapi> topLevelJsonapiMap) {
        Map<String, Object> objectMap = new HashMap<>();
        topLevelJsonapiMap.forEach((url, topLevelJsonapi) -> {

            if (topLevelJsonapi.getData() != null || topLevelJsonapi.getData().length > 0) {

                for (Data data : topLevelJsonapi.getData()) {
                    objectMap.put("data_id", DataUtil.getDataId(data));
                    objectMap.put("data_type", DataUtil.getDataType(data));
                    objectMap.put("data_links", DataUtil.getDataLinks(data));

                    objectMap.put("attribute_drupal_internal__nid", DataUtil.getDataAttributeDrupalInternalNid(data));
                    objectMap.put("attribute_drupal_internal__vid", DataUtil.getDataAttributeDrupalInternalVid(data));
                    objectMap.put("attribute_langcode", DataUtil.getDataAttributeLangcode(data));
                    objectMap.put("attribute_revision_timestamp", DataUtil.getDataAttributeRevisionTimestamp(data));
                    objectMap.put("attribute_revision_log", DataUtil.getDataAttributeRevisionLog(data));
                    objectMap.put("attribute_status", DataUtil.getDataAttributeStatus(data));
                    objectMap.put("attribute_changed", DataUtil.getDataAttributeChanged(data));
                    objectMap.put("attribute_defaultLangcode", DataUtil.getDataAttributeDefaultLangcode(data));
                    objectMap.put("attribute_revision_translation_affected", DataUtil.getDataAttributeRevisionTranslationAffected(data));
                    objectMap.put("attribute_path", DataUtil.getDataAttributePath(data));
                    objectMap.put("attribute_fields", DataUtil.getDataAttributeFields(data));

                    objectMap.put("relationships_fields", DataUtil.getDataRelationships(data));
                }

                objectMap.put("links", DataUtil.getLinks(topLevelJsonapi));
                objectMap.put("meta", DataUtil.getMeta(topLevelJsonapi));
                objectMap.put("errors", DataUtil.getErrors(topLevelJsonapi));
                objectMap.put("included", DataUtil.getIncluded(topLevelJsonapi));
            }
        });

        return objectMap;
    }
}
