package com.lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Jsonapi implements Serializable {
    private String version;
    private Meta meta;
}
