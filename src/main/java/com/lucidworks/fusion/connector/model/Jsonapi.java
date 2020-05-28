package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Type("jsonApi")
public class Jsonapi {
    private String version;

    @com.github.jasminb.jsonapi.annotations.Meta
    private Meta meta;
}
