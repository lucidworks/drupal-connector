package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Type("jsonApi")
public class Jsonapi implements Serializable {
    private String version;

    @com.github.jasminb.jsonapi.annotations.Meta
    private Meta meta;
}
