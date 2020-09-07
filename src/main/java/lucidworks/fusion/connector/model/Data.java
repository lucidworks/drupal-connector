package lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ToString
public class Data implements Serializable {
    private String id;
    private String type;
    private Map<String, LinkHref> Links;
    private Attributes attributes;
    private Relationships relationships;
}
