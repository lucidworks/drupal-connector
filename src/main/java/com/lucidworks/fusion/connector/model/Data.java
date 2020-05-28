package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.Links;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Type("data")
public class Data {
    @Id
    private String id;
    private String type;

    @com.github.jasminb.jsonapi.annotations.Links
    private Links links;

    private Attributes attributes;
    private Relationships relationships;
}
