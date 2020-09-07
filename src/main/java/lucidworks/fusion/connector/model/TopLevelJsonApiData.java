package lucidworks.fusion.connector.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class TopLevelJsonApiData extends TopLevelJsonapi implements Serializable {
    private lucidworks.fusion.connector.model.Data data;

    @Override
    public Data[] getData() {
        return new Data[]{this.data};
    }

}
