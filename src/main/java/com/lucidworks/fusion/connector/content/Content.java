package com.lucidworks.fusion.connector.content;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
public class Content {

    private Map<String, ContentEntry> entries;

    @JsonCreator
    public Content(
            @JsonProperty("entries") Map<String, ContentEntry> entries
    ) {
        this.entries = entries;
    }

}
