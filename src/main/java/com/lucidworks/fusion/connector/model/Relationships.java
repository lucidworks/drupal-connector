package com.lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relationships {
    private Vid vid;
    private RevisionUser revisionUser;
    private Parent parent;
    private ContentTranslationUid contentTranslationUid;
}
