package com.lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attributes {
    private Integer drupalInternalTid;
    private Integer drupalInternalRevisionId;
    private String language;
    private String revisionCreated;
    private String revisionLogMessage;
    private boolean status;
    private String name;
    private String description;
    private Integer weight;
    private String changed;
    private boolean defaultLangcode;
    private boolean revisionTranslationAffected;
    private Path path;
}
