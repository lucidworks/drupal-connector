package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ToString
@Type("data")
public class Data implements Serializable {
    @Id
    private String id;
    private String type;
    private Map<String, LinkHref> Links;
    private Attributes attributes;
    private Relationships relationships;
}
