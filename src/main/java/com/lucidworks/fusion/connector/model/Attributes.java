package com.lucidworks.fusion.connector.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class Attributes {

    @JsonProperty("drupal_internal__nid")
    private Integer drupalInternalTid;

    @JsonProperty("drupal_internal__vid")
    private Integer drupalInternalVid;

    private String langcode;

    @JsonProperty("revision_timestamp")
    private String revisionCreated;

    @JsonProperty("revision_log")
    private String revisionLogMessage;

    private boolean status;
    private String changed;

    @JsonProperty("default_langcode")
    private boolean defaultLangcode;
    @JsonProperty("revision_translation_affected")
    private boolean revisionTranslationAffected;

    private Path path;

    private Map<String, Object> fields = new HashMap<>();

    @JsonAnySetter
    public void setFields(String key, Object value) {
        fields.put(key, value);
    }
}
