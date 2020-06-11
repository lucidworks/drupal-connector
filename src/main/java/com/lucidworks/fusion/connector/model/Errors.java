package com.lucidworks.fusion.connector.model;

import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Type("errors")
public class Errors implements Serializable {
    private String title;
    private String status;
    private String detail;
}
