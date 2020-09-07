package lucidworks.fusion.connector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class DrupalLoginRequest {
    private String name;
    private String pass;

    public String getJson() {
        String jsonString = new StringBuilder()
                .append("{")
                .append("\"name\":\"" + name + "\",")
                .append("\"pass\":\"" + pass + "\"")
                .append("}").toString();
        return jsonString;
    }
}
