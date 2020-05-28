package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.Links;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Type("meta")
public class Meta {
    @com.github.jasminb.jsonapi.annotations.Links
    private Links links;
    //omitted - String detail, Links
}
