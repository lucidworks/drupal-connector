package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@Type("meta")
public class Meta {
    private Map<String, LinkHref> Links;
}
