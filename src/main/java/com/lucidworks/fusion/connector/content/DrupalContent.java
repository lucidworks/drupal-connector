package com.lucidworks.fusion.connector.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Map;

@Getter
public class DrupalContent {

    private Map<String, DrupalContentEntry> entries;

    @JsonCreator
    public DrupalContent(
            @JsonProperty("entries") Map<String, DrupalContentEntry> entries
    ) {
        this.entries = entries;
    }

    public Map<String, Object> getMapWithObject() {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        builder.putAll(entries);

        return builder.build();
    }

}
