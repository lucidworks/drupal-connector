package com.lucidworks.fusion.connector.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ContentEntry {

    private String id;
    private String title;
    private long lastUpdated;

    @JsonCreator
    public ContentEntry(
            @JsonProperty("id") String id,
            @JsonProperty("title") String title,
            @JsonProperty("lastUpdated") long lastUpdated
    ) {
        this.id = id;
        this.title = title;
        this.lastUpdated = lastUpdated;
    }

}
