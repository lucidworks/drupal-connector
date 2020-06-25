package com.lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class RelationshipFields {
    private Object data;
    private Map<String, LinkHref> Links;
}
