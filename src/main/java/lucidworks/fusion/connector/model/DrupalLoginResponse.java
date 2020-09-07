package lucidworks.fusion.connector.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class DrupalLoginResponse implements Serializable {
    @JsonProperty("current_user")
    private Map<String, Object> currentUser;
    @JsonProperty("csrf_token")
    private String csrfToken;
    @JsonProperty("logout_token")
    private String logoutToken;
    @JsonIgnore
    private String cookie;
}
