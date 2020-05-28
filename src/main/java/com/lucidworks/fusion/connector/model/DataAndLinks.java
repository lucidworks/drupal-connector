package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.Links;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataAndLinks {
    private Data data;

    @com.github.jasminb.jsonapi.annotations.Links
    private Links links;
}
