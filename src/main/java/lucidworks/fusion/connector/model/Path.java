package lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Path implements Serializable {
    private String alias;
    private Integer pid;
    private String langcode;
}
