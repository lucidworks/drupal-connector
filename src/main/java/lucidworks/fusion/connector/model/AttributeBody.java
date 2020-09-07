package lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttributeBody {
    private String value;
    private String format;
    private String processed;
    private String summary;
}
