package dmit2015.restclient.openweathermap;

import java.util.LinkedHashMap;
import java.util.Map;
import jakarta.annotation.Generated;

@Generated("jsonschema2pojo")
public class Clouds {

    private Integer all;
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
