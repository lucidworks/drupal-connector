package lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@ToString
public class Meta implements Serializable {
    private Map<String, LinkHref> Links;
    private Object omitted;
}
