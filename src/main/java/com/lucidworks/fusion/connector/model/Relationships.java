package com.lucidworks.fusion.connector.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class Relationships implements Serializable {

    private Map<String, RelationshipFields> fields = new HashMap<>();

    @JsonAnySetter
    public void setFields(String key, RelationshipFields value) {
        fields.put(key, value);
    }
}
