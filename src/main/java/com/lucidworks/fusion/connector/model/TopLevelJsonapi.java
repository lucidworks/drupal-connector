package com.lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ToString
public class TopLevelJsonapi implements Serializable {
    private Jsonapi jsonapi;
    private Data[] data;
    private Map<String, LinkHref> Links;
    private Meta meta;
    private Errors errors;
    private Included included;
}
