package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.Links;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Type("topLevel")
public class TopLevelJsonapi {
    private Jsonapi jsonapi;
    private Data data;
    private Errors errors;

    @com.github.jasminb.jsonapi.annotations.Meta
    private Meta meta;

    @com.github.jasminb.jsonapi.annotations.Links
    private Links links;
    private Included included;
}
