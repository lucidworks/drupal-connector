package lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Errors implements Serializable {
    private String title;
    private String status;
    private String detail;
}
