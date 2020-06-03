package com.lucidworks.fusion.connector.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DrupalContentEntry {
    private String url;
    private String content;
    private long lastUpdated;

    @JsonCreator
    public DrupalContentEntry(
            @JsonProperty("url") String url,
            @JsonProperty("content") String content,
            @JsonProperty("lastUpdated") long lastUpdated
    ) {
        this.url = url;
        this.content = content;
        this.lastUpdated = lastUpdated;
    }
}
