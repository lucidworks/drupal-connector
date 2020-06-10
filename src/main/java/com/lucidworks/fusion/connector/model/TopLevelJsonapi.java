package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@Type("topLevel")
public class TopLevelJsonapi {
    private Jsonapi jsonapi;
    private Data[] data;
    private Map<String, LinkHref> Links;

    private Meta meta;
    private Errors errors;
    private Included included;

//    @com.github.jasminb.jsonapi.annotations.Meta
//    private Meta meta;
//    @com.github.jasminb.jsonapi.annotations.Links
//    private Links links;


}
