package com.lucidworks.fusion.connector.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class Relationships {

    private Map<String, TopLevelJsonapi> fields = new HashMap<>();

    @JsonAnySetter
    public void setFields(String key, TopLevelJsonapi value) {
        fields.put(key, value);
    }
}
